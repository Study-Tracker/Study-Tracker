/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.studytracker.config;

import io.studytracker.config.properties.SingleSignOnProperties;
import io.studytracker.config.properties.SingleSignOnProperties.SamlKeystoreProperties;
import io.studytracker.model.User;
import io.studytracker.security.AppUserDetailsService;
import io.studytracker.security.DatabaseAuthenticationProvider;
import io.studytracker.security.TokenFilter;
import io.studytracker.security.TokenUtils;
import io.studytracker.security.UserAuthenticationSuccessHandler;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.HstsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;
import org.springframework.security.saml2.provider.service.registration.Saml2MessageBinding;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
    prePostEnabled = true,
    securedEnabled = true,
    jsr250Enabled = true
)
public class WebSecurityConfiguration {

  @Autowired
  private AppUserDetailsService appUserDetailsService;

  @Autowired
  private SingleSignOnProperties ssoProperties;

  @Bean
  public UserAuthenticationSuccessHandler userAuthenticationSuccessHandler() {
    return new UserAuthenticationSuccessHandler();
  }

  @Bean
  public AuditorAware<User> auditorAware() {
    return new UserServiceAuditor();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DatabaseAuthenticationProvider dbAuthProvider() {
    return new DatabaseAuthenticationProvider(passwordEncoder(), appUserDetailsService);
  }

  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder =
        http.getSharedObject(AuthenticationManagerBuilder.class);
    authenticationManagerBuilder.authenticationProvider(dbAuthProvider());
    return authenticationManagerBuilder.build();
  }

  /**
   * Creates SAML2 signing credentials from the keystore
   */
  private Saml2X509Credential createSaml2SigningCredential() throws Exception {
    SamlKeystoreProperties keystore = ssoProperties.getSaml().getKeystore();
    String keystoreLocation = keystore.getLocation();

    // Enforce absolute paths for non-relative paths
    if (!keystoreLocation.startsWith("classpath:") 
        && !keystoreLocation.startsWith("/") 
        && !keystoreLocation.startsWith("file:")) {
      keystoreLocation = "file:" + keystoreLocation;
    }

    DefaultResourceLoader loader = new DefaultResourceLoader();
    Resource storeFile = loader.getResource(keystoreLocation);
    if (!storeFile.exists()) {
      throw new IllegalArgumentException("Keystore file not found: " + keystoreLocation);
    }

    KeyStore store = KeyStore.getInstance("JKS");
    try (InputStream is = storeFile.getInputStream()) {
      store.load(is, keystore.getPassword().toCharArray());
    }

    RSAPrivateKey privateKey = (RSAPrivateKey) store.getKey(
        keystore.getAlias(), 
        keystore.getPassword().toCharArray()
    );

    X509Certificate certificate = (X509Certificate) store.getCertificate(keystore.getAlias());

    return Saml2X509Credential.signing(privateKey, certificate);
  }

  /**
   * Bean for Okta SAML signing credentials
   */
  @Bean
  @ConditionalOnProperty(name = "security.sso", havingValue = "okta-saml", matchIfMissing = false)
  public Saml2X509Credential oktaSaml2SigningCredential() throws Exception {
    return createSaml2SigningCredential();
  }

  /**
   * Bean for Entra ID SAML signing credentials
   */
  @Bean
  @ConditionalOnProperty(name = "security.sso", havingValue = "entra-saml", matchIfMissing = false)
  public Saml2X509Credential entraSaml2SigningCredential() throws Exception {
    return createSaml2SigningCredential();
  }

  /**
   * Configures the SAML2 Service Provider registration for Okta
   */
  @Bean
  @ConditionalOnProperty(name = "security.sso", havingValue = "okta-saml")
  public RelyingPartyRegistrationRepository oktaRelyingPartyRegistrationRepository(
      Saml2X509Credential oktaSaml2SigningCredential) {

    RelyingPartyRegistration registration = RelyingPartyRegistrations
        .fromMetadataLocation(ssoProperties.getSaml().getMetadataUrl())
        .registrationId("okta")
        .entityId(ssoProperties.getSaml().getAudience())
        .assertionConsumerServiceLocation(
            ssoProperties.getSaml().getMetadataBaseUrl() + "/login/saml2/sso/okta")
        .signingX509Credentials(c -> c.add(oktaSaml2SigningCredential))
        .build();

    return new InMemoryRelyingPartyRegistrationRepository(registration);
  }

  /**
   * Configures the SAML2 Service Provider registration for Entra ID
   */
  @Bean
  @ConditionalOnProperty(name = "security.sso", havingValue = "entra-saml")
  public RelyingPartyRegistrationRepository entraRelyingPartyRegistrationRepository(
      Saml2X509Credential entraSaml2SigningCredential) {

    RelyingPartyRegistration registration = RelyingPartyRegistrations
        .fromMetadataLocation(ssoProperties.getSaml().getMetadataUrl())
        .registrationId("entra")
        .entityId(ssoProperties.getSaml().getAudience())
        .assertionConsumerServiceLocation(
            ssoProperties.getSaml().getMetadataBaseUrl() + "/login/saml2/sso/entra")
        .signingX509Credentials(c -> c.add(entraSaml2SigningCredential))
        .assertionConsumerServiceBinding(Saml2MessageBinding.REDIRECT)
        .build();

    return new InMemoryRelyingPartyRegistrationRepository(registration);
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("*"));
    configuration.setAllowedMethods(Arrays.asList("*"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//      auth.authenticationProvider(dbAuthProvider);
//    }

//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//      AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
//      builder.authenticationProvider(dbAuthProvider);
//      return builder.build();
//    }

  @Bean
  public TokenUtils tokenUtils() {
    return new TokenUtils();
  }

  @Bean
  public TokenFilter tokenFilter() {
      return new TokenFilter();
    }

  @Bean
  @Order(1)
  public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
    http
        .securityMatcher("/api/v1/**")
        .authorizeHttpRequests(auth ->
            auth.anyRequest().authenticated())
        .csrf(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .cors(cors -> cors
            .configurationSource(corsConfigurationSource()))
        .exceptionHandling(
            h -> h
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .sessionManagement(m -> m
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//        .authenticationManager(authenticationManager())
    ;
    http.addFilterBefore(tokenFilter(), UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  @Order(2)
  @ConditionalOnProperty(name = "security.sso", havingValue = "none", matchIfMissing = true)
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/static/**", "/error", "/login", "/auth/**").permitAll()
            .anyRequest().fullyAuthenticated())
        .formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/auth/login")
            .defaultSuccessUrl("/"))
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")
            .invalidateHttpSession(true))
        .headers(headers -> headers
            .frameOptions(FrameOptionsConfig::disable)
            .httpStrictTransportSecurity(HstsConfig::disable))
        .csrf(AbstractHttpConfigurer::disable);
//        .csrf(csrf -> csrf
//            .ignoringRequestMatchers("/auth/**", "/login")
//            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
    ;
    return http.build();
  }

  /**
   * Configures the Okta SAML2 security filter chain
   */
  @Bean
  @Order(3)
  @ConditionalOnProperty(name = "security.sso", havingValue = "okta-saml")
  public SecurityFilterChain oktaSamlSecurityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/static/**", "/error", "/login/**", "/saml/**", "/auth/**").permitAll()
            .anyRequest().fullyAuthenticated())
        .formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/auth/login")
            .defaultSuccessUrl("/"))
        .saml2Login(saml2 -> saml2
            .loginPage("/login")
            .defaultSuccessUrl("/")
            .failureUrl("/error"))
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")
            .invalidateHttpSession(true))
        .saml2Logout(Customizer.withDefaults())
        .headers(headers -> headers
            .frameOptions(FrameOptionsConfig::disable)
            .httpStrictTransportSecurity(HstsConfig::disable))
        .csrf(AbstractHttpConfigurer::disable);
//        .csrf(csrf -> csrf
//            .ignoringRequestMatchers("/login", "/auth/**")
//            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));

    return http.build();
  }

  /**
   * Configures the Entra ID SAML2 security filter chain
   */
  @Bean
  @Order(3)
  @ConditionalOnProperty(name = "security.sso", havingValue = "entra-saml")
  public SecurityFilterChain entraSamlSecurityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/static/**", "/error", "/login/**", "/saml/**", "/auth/**").permitAll()
            .anyRequest().fullyAuthenticated())
        .formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/auth/login")
            .defaultSuccessUrl("/"))
        .saml2Login(saml2 -> saml2
            .loginPage("/login")
            .defaultSuccessUrl("/")
            .failureUrl("/error"))
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")
            .invalidateHttpSession(true))
        .saml2Logout(Customizer.withDefaults())
        .headers(headers -> headers
            .frameOptions(FrameOptionsConfig::disable)
            .httpStrictTransportSecurity(HstsConfig::disable))
        .csrf(AbstractHttpConfigurer::disable);
//        .csrf(csrf -> csrf
//            .ignoringRequestMatchers("/login", "/auth/**")
//            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));

    return http.build();
  }

}
