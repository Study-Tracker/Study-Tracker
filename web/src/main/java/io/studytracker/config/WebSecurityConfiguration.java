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

import io.studytracker.model.User;
import io.studytracker.security.AppUserDetailsService;
import io.studytracker.security.DatabaseAuthenticationProvider;
import io.studytracker.security.TokenFilter;
import io.studytracker.security.TokenUtils;
import io.studytracker.security.UserAuthenticationSuccessHandler;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private DatabaseAuthenticationProvider dbAuthProvider;

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
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
    builder.authenticationProvider(dbAuthProvider);
    return builder.build();
  }

  @Configuration
  @Order(1)
  public static class PublicApiSecurityConfiguration  {

    @Autowired
    private AppUserDetailsService appUserDetailsService;

//    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
//    @Autowired
//    private DatabaseAuthenticationProvider dbAuthProvider;

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
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
      http
          .securityMatcher("/api/v1/**")
          .authorizeHttpRequests(auth ->
              auth.anyRequest().authenticated())
          .csrf(csrf -> csrf.disable())
          .httpBasic(basic -> basic.disable())
          .cors(cors -> cors
              .configurationSource(corsConfigurationSource()))
          .exceptionHandling(
              h -> h
                  .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
          .sessionManagement(m -> m
              .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .userDetailsService(appUserDetailsService)
          .sessionManagement(s -> s
              .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      ;
      http.addFilterBefore(tokenFilter(), UsernamePasswordAuthenticationFilter.class);
      return http.build();
    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//      http.antMatcher("/api/v1/**")
//          .csrf().disable()
//          .httpBasic().disable()
//          .cors()
//          .and()
//          .authorizeRequests()
//          .anyRequest().authenticated()
//          .and()
//          .userDetailsService(appUserDetailsService)
//          .exceptionHandling()
//          .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
//          .and()
//          .sessionManagement()
//          .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//
//      http.addFilterBefore(tokenFilter(), UsernamePasswordAuthenticationFilter.class);
//
//    }

  }

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Configuration
  @Order(2)
  @ConditionalOnProperty(name = "security.sso", havingValue = "none", matchIfMissing = true)
  public static class WebAppSecurityConfiguration {

//    @Autowired private DatabaseAuthenticationProvider dbAuthProvider;

//    @Bean
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//      return super.authenticationManagerBean();
//    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//      auth.authenticationProvider(dbAuthProvider);
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      http
          .authorizeHttpRequests(auth -> auth
              .requestMatchers("/static/**", "/error", "/login", "/auth/**").permitAll()
              .anyRequest().fullyAuthenticated())
          .formLogin(form -> form
              .loginPage("/login")
              .defaultSuccessUrl("/"))
          .logout(logout -> logout
              .logoutUrl("/logout")
              .logoutSuccessUrl("/")
              .invalidateHttpSession(true))
          .headers(headers -> headers
              .frameOptions(f -> f.disable())
              .httpStrictTransportSecurity(t -> t.disable()))
          .csrf(csrf -> csrf
              .ignoringRequestMatchers("/auth/**")
              .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
      ;
      return http.build();
    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//      http.authorizeRequests()
//          .antMatchers("/static/**", "/error", "/login", "/auth/**").permitAll()
//          .anyRequest().fullyAuthenticated()
//          .and()
//          .formLogin()
//            .loginPage("/login")
//            .defaultSuccessUrl("/")
//            .permitAll()
//          .and()
//          .logout()
//            .logoutUrl("/logout")
//            .logoutSuccessUrl("/")
//            .invalidateHttpSession(true)
//          .and()
//          .headers()
//            .frameOptions().disable()
//            .httpStrictTransportSecurity().disable()
//          .and()
//          .csrf()
//            .ignoringAntMatchers("/auth/**")
//            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//      ;
//    }
  }

//  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
//  @Configuration
//  @Order(3)
//  @ConditionalOnProperty(name = "security.sso", havingValue = "okta-saml")
//  public static class WebOktaSSOAppSecurityConfiguration extends WebSecurityConfigurerAdapter
//      implements DisposableBean, InitializingBean {
//
//    private static final Logger LOGGER =
//        LoggerFactory.getLogger(WebOktaSSOAppSecurityConfiguration.class);
//
//    @Autowired
//    private SingleSignOnProperties properties;
//
////    @Value("${saml.audience}")
////    private String samlAudience;
////
////    @Value("${saml.keystore.location}")
////    private String samlKeystoreLocation;
////
////    @Value("${saml.keystore.password}")
////    private String samlKeystorePassword;
////
////    @Value("${saml.keystore.alias}")
////    private String samlKeystoreAlias;
////
////    @Value("${saml.idp}")
////    private String defaultIdp;
////
////    @Value("${saml.metadata-url}")
////    private String metadataUrl;
////
////    @Value("${saml.metadata-base-url}")
////    private String metadataBaseUrl;
////
////    @Value("${saml.max-authentication-age:86400}")
////    private long maxAuthenticationAge;
//
//    @Autowired private AppUserDetailsService appUserDetailsService;
//
//    @Autowired private DatabaseAuthenticationProvider dbAuthProvider;
//
//    private Timer backgroundTaskTimer;
//
//    private MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager;
//
//    @Bean
//    @Qualifier("saml")
//    public Timer getBackgroundTaskTimer() {
//      return backgroundTaskTimer;
//    }
//
//    @Bean
//    @Qualifier("saml")
//    public MultiThreadedHttpConnectionManager getMultiThreadedHttpConnectionManager() {
//      return multiThreadedHttpConnectionManager;
//    }
//
//    @Bean
//    public VelocityEngine velocityEngine() {
//      return VelocityFactory.getEngine();
//    }
//
//    @Bean(initMethod = "initialize")
//    public StaticBasicParserPool parserPool() {
//      return new StaticBasicParserPool();
//    }
//
//    @Bean(name = "parserPoolHolder")
//    public ParserPoolHolder parserPoolHolder() {
//      return new ParserPoolHolder();
//    }
//
//    @Bean
//    public HttpClient httpClient() {
//      return new HttpClient(multiThreadedHttpConnectionManager);
//    }
//
//    @Bean
//    public SAMLAuthenticationProvider samlAuthenticationProvider() {
//      SAMLAuthenticationProvider samlAuthenticationProvider = new SAMLAuthenticationProvider();
//      samlAuthenticationProvider.setUserDetails(appUserDetailsService);
//      samlAuthenticationProvider.setForcePrincipalAsString(false);
//      return samlAuthenticationProvider;
//    }
//
//    @Bean
//    public SAMLContextProviderImpl contextProvider() {
//      return new SAMLContextProviderImpl();
//    }
//
//    @Bean
//    public static SAMLBootstrap sAMLBootstrap() {
//      return new SAMLBootstrap();
//    }
//
//    @Bean
//    public SAMLDefaultLogger samlLogger() {
//      return new SAMLDefaultLogger();
//    }
//
//    @Bean
//    public WebSSOProfileConsumer webSSOprofileConsumer() {
//      WebSSOProfileConsumerImpl consumer = new WebSSOProfileConsumerImpl();
//      consumer.setMaxAuthenticationAge(properties.getSaml().getMaxAuthenticationAge());
//      return consumer;
//    }
//
//    @Bean
//    @Qualifier("hokWebSSOprofileConsumer")
//    public WebSSOProfileConsumerHoKImpl hokWebSSOProfileConsumer() {
//      return new WebSSOProfileConsumerHoKImpl();
//    }
//
//    @Bean
//    public WebSSOProfile webSSOprofile() {
//      return new WebSSOProfileImpl();
//    }
//
//    @Bean
//    public WebSSOProfileConsumerHoKImpl hokWebSSOProfile() {
//      return new WebSSOProfileConsumerHoKImpl();
//    }
//
//    @Bean
//    public WebSSOProfileECPImpl ecpProfile() {
//      return new WebSSOProfileECPImpl();
//    }
//
//    @Bean
//    public SingleLogoutProfile logoutProfile() {
//      return new SingleLogoutProfileImpl();
//    }
//
//    @Bean
//    public KeyManager keyManager() {
//
//      DefaultResourceLoader loader = new DefaultResourceLoader();
//      SamlKeystoreProperties keystore = properties.getSaml().getKeystore();
//      String samlKeystoreLocation = properties.getSaml().getKeystore().getLocation();
//
//      // Enforce absolute paths for non-relative paths
//      if (!samlKeystoreLocation.startsWith("classpath:")
//          && !samlKeystoreLocation.startsWith("/")
//          && !samlKeystoreLocation.startsWith("file:")) {
//        samlKeystoreLocation = "file:" + samlKeystoreLocation;
//      }
//      Resource storeFile = loader.getResource(samlKeystoreLocation);
//      if (!storeFile.exists()) {
//        throw new IllegalArgumentException(
//            "Keystore file not found: " + samlKeystoreLocation);
//      }
//
//      Map<String, String> passwords = new HashMap<>();
//      passwords.put(keystore.getAlias(), keystore.getPassword());
//      return new JKSKeyManager(storeFile, keystore.getPassword(), passwords, keystore.getAlias());
//
//    }
//
//    @Bean
//    public WebSSOProfileOptions defaultWebSSOProfileOptions() {
//      WebSSOProfileOptions webSSOProfileOptions = new WebSSOProfileOptions();
//      webSSOProfileOptions.setIncludeScoping(false);
//      return webSSOProfileOptions;
//    }
//
//    @Bean
//    public SAMLEntryPoint samlEntryPoint() {
//      SAMLEntryPoint samlEntryPoint = new SAMLEntryPoint();
//      samlEntryPoint.setDefaultProfileOptions(defaultWebSSOProfileOptions());
//      return samlEntryPoint;
//    }
//
//    @Bean
//    public ExtendedMetadata extendedMetadata() {
//      ExtendedMetadata extendedMetadata = new ExtendedMetadata();
//      extendedMetadata.setIdpDiscoveryEnabled(true);
//      extendedMetadata.setSigningAlgorithm("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
//      extendedMetadata.setSignMetadata(true);
//      extendedMetadata.setEcpEnabled(true);
//      return extendedMetadata;
//    }
//
//    @Bean
//    public SAMLDiscovery samlDiscovery() {
//      SAMLDiscovery idpDiscovery = new SAMLDiscovery();
//      idpDiscovery.setIdpSelectionPath("/saml/discovery");
//      idpDiscovery.setSamlEntryPoint(samlEntryPoint());
//      return idpDiscovery;
//    }
//
//    @Bean
//    @Qualifier("okta")
//    public ExtendedMetadataDelegate oktaExtendedMetadataProvider(Timer backgroundTaskTimer)
//        throws MetadataProviderException {
//      HTTPMetadataProvider metadataProvider =
//          new HTTPMetadataProvider(backgroundTaskTimer, httpClient(), properties.getSaml().getMetadataUrl());
//      metadataProvider.setParserPool(parserPool());
//      metadataProvider.initialize();
//
//      ExtendedMetadataDelegate extendedMetadataDelegate =
//          new ExtendedMetadataDelegate(metadataProvider, extendedMetadata());
//      extendedMetadataDelegate.setMetadataTrustCheck(true);
//      extendedMetadataDelegate.setMetadataRequireSignature(false);
//
//      backgroundTaskTimer.purge();
//      return extendedMetadataDelegate;
//    }
//
//    @Bean
//    @Qualifier("metadata")
//    public CachingMetadataManager metadata(ExtendedMetadataDelegate oktaExtendedMetadataProvider)
//        throws MetadataProviderException, ResourceException {
//      List<MetadataProvider> providers = new ArrayList<>();
//      providers.add(oktaExtendedMetadataProvider);
//      CachingMetadataManager metadataManager = new CachingMetadataManager(providers);
//      metadataManager.setDefaultIDP(properties.getSaml().getIdp());
//      return metadataManager;
//    }
//
//    @Bean
//    public MetadataDisplayFilter metadataDisplayFilter() {
//      return new MetadataDisplayFilter();
//    }
//
//    @Bean
//    public SimpleUrlLogoutSuccessHandler successLogoutHandler() {
//      SimpleUrlLogoutSuccessHandler successLogoutHandler = new SimpleUrlLogoutSuccessHandler();
//      successLogoutHandler.setDefaultTargetUrl("/");
//      return successLogoutHandler;
//    }
//
//    @Bean
//    public SecurityContextLogoutHandler logoutHandler() {
//      SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
//      logoutHandler.setInvalidateHttpSession(true);
//      logoutHandler.setClearAuthentication(true);
//      return logoutHandler;
//    }
//
//    @Bean
//    public SAMLLogoutProcessingFilter samlLogoutProcessingFilter() {
//      return new SAMLLogoutProcessingFilter(successLogoutHandler(), logoutHandler());
//    }
//
//    @Bean
//    public SAMLLogoutFilter samlLogoutFilter() {
//      return new SAMLLogoutFilter(
//          successLogoutHandler(),
//          new LogoutHandler[] {logoutHandler()},
//          new LogoutHandler[] {logoutHandler()});
//    }
//
//    private ArtifactResolutionProfile artifactResolutionProfile() {
//      final ArtifactResolutionProfileImpl artifactResolutionProfile =
//          new ArtifactResolutionProfileImpl(httpClient());
//      artifactResolutionProfile.setProcessor(new SAMLProcessorImpl(soapBinding()));
//      return artifactResolutionProfile;
//    }
//
//    @Bean
//    public HTTPArtifactBinding artifactBinding(
//        ParserPool parserPool, VelocityEngine velocityEngine) {
//      return new HTTPArtifactBinding(parserPool, velocityEngine, artifactResolutionProfile());
//    }
//
//    @Bean
//    public HTTPSOAP11Binding soapBinding() {
//      return new HTTPSOAP11Binding(parserPool());
//    }
//
//    @Bean
//    public HTTPPostBinding httpPostBinding() {
//      return new HTTPPostBinding(parserPool(), velocityEngine());
//    }
//
//    @Bean
//    public HTTPRedirectDeflateBinding httpRedirectDeflateBinding() {
//      return new HTTPRedirectDeflateBinding(parserPool());
//    }
//
//    @Bean
//    public HTTPSOAP11Binding httpSOAP11Binding() {
//      return new HTTPSOAP11Binding(parserPool());
//    }
//
//    @Bean
//    public HTTPPAOS11Binding httpPAOS11Binding() {
//      return new HTTPPAOS11Binding(parserPool());
//    }
//
//    // Processor
//    @Bean
//    public SAMLProcessorImpl processor() {
//      Collection<SAMLBinding> bindings = new ArrayList<>();
//      bindings.add(httpRedirectDeflateBinding());
//      bindings.add(httpPostBinding());
//      bindings.add(artifactBinding(parserPool(), velocityEngine()));
//      bindings.add(httpSOAP11Binding());
//      bindings.add(httpPAOS11Binding());
//      return new SAMLProcessorImpl(bindings);
//    }
//
//    public MetadataGenerator metadataGenerator() {
//      MetadataGenerator metadataGenerator = new MetadataGenerator();
//      metadataGenerator.setEntityId(properties.getSaml().getAudience());
//      metadataGenerator.setExtendedMetadata(extendedMetadata());
//      metadataGenerator.setIncludeDiscoveryExtension(false);
//      metadataGenerator.setKeyManager(keyManager());
//      metadataGenerator.setEntityBaseURL(properties.getSaml().getMetadataBaseUrl());
//      return metadataGenerator;
//    }
//
//    // Handler deciding where to redirect user after successful login
//    @Bean
//    @Qualifier("saml")
//    public SavedRequestAwareAuthenticationSuccessHandler samlAuthSuccessHandler() {
//      SavedRequestAwareAuthenticationSuccessHandler successRedirectHandler =
//          new SavedRequestAwareAuthenticationSuccessHandler();
//      successRedirectHandler.setDefaultTargetUrl("/");
//      return successRedirectHandler;
//    }
//
//    // Handler deciding where to redirect user after failed login
//    @Bean
//    @Qualifier("saml")
//    public SimpleUrlAuthenticationFailureHandler samlAuthFailureHandler() {
//      SimpleUrlAuthenticationFailureHandler failureHandler =
//          new SimpleUrlAuthenticationFailureHandler();
//      failureHandler.setUseForward(true);
//      failureHandler.setDefaultFailureUrl("/error");
//      return failureHandler;
//    }
//
//    @Bean
//    public MetadataGeneratorFilter metadataGeneratorFilter() {
//      return new MetadataGeneratorFilter(metadataGenerator());
//    }
//
//    @Bean
//    public SAMLWebSSOHoKProcessingFilter samlWebSSOHoKProcessingFilter() throws Exception {
//      SAMLWebSSOHoKProcessingFilter samlWebSSOHoKProcessingFilter =
//          new SAMLWebSSOHoKProcessingFilter();
//      samlWebSSOHoKProcessingFilter.setAuthenticationSuccessHandler(samlAuthSuccessHandler());
//      samlWebSSOHoKProcessingFilter.setAuthenticationManager(authenticationManager());
//      samlWebSSOHoKProcessingFilter.setAuthenticationFailureHandler(samlAuthFailureHandler());
//      return samlWebSSOHoKProcessingFilter;
//    }
//
//    // Processing filter for WebSSO profile messages
//    @Bean
//    public SAMLProcessingFilter samlWebSSOProcessingFilter() throws Exception {
//      SAMLProcessingFilter samlWebSSOProcessingFilter = new SAMLProcessingFilter();
//      samlWebSSOProcessingFilter.setAuthenticationManager(authenticationManager());
//      samlWebSSOProcessingFilter.setAuthenticationSuccessHandler(samlAuthSuccessHandler());
//      samlWebSSOProcessingFilter.setAuthenticationFailureHandler(samlAuthFailureHandler());
//      return samlWebSSOProcessingFilter;
//    }
//
//    /**
//     * Define the security filter chain in order to support SSO Auth by using SAML 2.0
//     *
//     * @return Filter chain proxy
//     */
//    @Bean
//    public FilterChainProxy samlFilter() throws Exception {
//      List<SecurityFilterChain> chains = new ArrayList<>();
//      chains.add(
//          new DefaultSecurityFilterChain(
//              new AntPathRequestMatcher("/saml/login/**"), samlEntryPoint()));
//      chains.add(
//          new DefaultSecurityFilterChain(
//              new AntPathRequestMatcher("/saml/logout/**"), samlLogoutFilter()));
//      chains.add(
//          new DefaultSecurityFilterChain(
//              new AntPathRequestMatcher("/saml/metadata/**"), metadataDisplayFilter()));
//      chains.add(
//          new DefaultSecurityFilterChain(
//              new AntPathRequestMatcher("/saml/SSO/**"), samlWebSSOProcessingFilter()));
//      chains.add(
//          new DefaultSecurityFilterChain(
//              new AntPathRequestMatcher("/saml/SSOHoK/**"), samlWebSSOHoKProcessingFilter()));
//      chains.add(
//          new DefaultSecurityFilterChain(
//              new AntPathRequestMatcher("/saml/SingleLogout/**"), samlLogoutProcessingFilter()));
//      chains.add(
//          new DefaultSecurityFilterChain(
//              new AntPathRequestMatcher("/saml/discovery/**"), samlDiscovery()));
//      return new FilterChainProxy(chains);
//    }
//
//    @Bean
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//      return super.authenticationManagerBean();
//    }
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//      auth.authenticationProvider(dbAuthProvider);
//      auth.authenticationProvider(samlAuthenticationProvider());
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//      http.authorizeRequests()
//          .antMatchers("/static/**")
//          .permitAll()
//          .antMatchers("/error")
//          .permitAll()
//          .antMatchers("/login")
//          .permitAll()
//          .antMatchers("/saml/**")
//          .permitAll()
//          .antMatchers("/auth/**")
//          .permitAll()
//          .anyRequest()
//          .fullyAuthenticated();
//
//      http.httpBasic()
//          .authenticationEntryPoint(
//              (request, response, exception) -> {
//                if (request.getRequestURI().endsWith("doSaml")) {
//                  samlEntryPoint().commence(request, response, exception);
//                } else {
//                  response.sendRedirect("/login");
//                }
//              });
//
//      http.addFilterBefore(metadataGeneratorFilter(), ChannelProcessingFilter.class)
//          .addFilterAfter(samlFilter(), BasicAuthenticationFilter.class)
//          .addFilterBefore(samlFilter(), CsrfFilter.class);
//
//      http.logout()
//          .addLogoutHandler(
//              (request, response, authentication) -> {
//                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//                if (auth instanceof AppUserDetails) {
//                  AppUserDetails userDetails = (AppUserDetails) auth.getPrincipal();
//                  if (userDetails.getAuthMethod() == AuthMethod.SAML) {
//                    try {
//                      response.sendRedirect("/saml/logout");
//                    } catch (Exception e) {
//                      LOGGER.error("Error processing logout for SAML user", e);
//                      throw new StudyTrackerException(e);
//                    }
//                  }
//                } else {
//                  LOGGER.warn("Unknown logout method for authentication: " + auth.getName());
//                }
//              });
//
//      http.headers().frameOptions().disable().httpStrictTransportSecurity().disable();
//
//      http
//          .csrf()
//          .ignoringAntMatchers("/login", "/auth/**")
//          .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
//    }
//
//    @Override
//    public void afterPropertiesSet() {
//      this.backgroundTaskTimer = new Timer(true);
//      this.multiThreadedHttpConnectionManager = new MultiThreadedHttpConnectionManager();
//    }
//
//    @Override
//    public void destroy() throws Exception {
//      this.backgroundTaskTimer.purge();
//      this.backgroundTaskTimer.cancel();
//      this.multiThreadedHttpConnectionManager.shutdown();
//    }
//  }

}
