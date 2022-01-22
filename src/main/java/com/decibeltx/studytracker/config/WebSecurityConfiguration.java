/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.decibeltx.studytracker.config;

import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.security.AppUserDetails;
import com.decibeltx.studytracker.security.AppUserDetails.AuthMethod;
import com.decibeltx.studytracker.security.AppUserDetailsService;
import com.decibeltx.studytracker.security.DatabaseAuthenticationProvider;
import com.decibeltx.studytracker.security.UserAuthenticationSuccessHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.velocity.app.VelocityEngine;
import org.opensaml.saml2.metadata.provider.HTTPMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.util.resource.ResourceException;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLBootstrap;
import org.springframework.security.saml.SAMLDiscovery;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.SAMLLogoutFilter;
import org.springframework.security.saml.SAMLLogoutProcessingFilter;
import org.springframework.security.saml.SAMLProcessingFilter;
import org.springframework.security.saml.SAMLWebSSOHoKProcessingFilter;
import org.springframework.security.saml.context.SAMLContextProviderImpl;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.log.SAMLDefaultLogger;
import org.springframework.security.saml.metadata.CachingMetadataManager;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;
import org.springframework.security.saml.metadata.MetadataDisplayFilter;
import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;
import org.springframework.security.saml.parser.ParserPoolHolder;
import org.springframework.security.saml.processor.HTTPArtifactBinding;
import org.springframework.security.saml.processor.HTTPPAOS11Binding;
import org.springframework.security.saml.processor.HTTPPostBinding;
import org.springframework.security.saml.processor.HTTPRedirectDeflateBinding;
import org.springframework.security.saml.processor.HTTPSOAP11Binding;
import org.springframework.security.saml.processor.SAMLBinding;
import org.springframework.security.saml.processor.SAMLProcessorImpl;
import org.springframework.security.saml.util.VelocityFactory;
import org.springframework.security.saml.websso.ArtifactResolutionProfile;
import org.springframework.security.saml.websso.ArtifactResolutionProfileImpl;
import org.springframework.security.saml.websso.SingleLogoutProfile;
import org.springframework.security.saml.websso.SingleLogoutProfileImpl;
import org.springframework.security.saml.websso.WebSSOProfile;
import org.springframework.security.saml.websso.WebSSOProfileConsumer;
import org.springframework.security.saml.websso.WebSSOProfileConsumerHoKImpl;
import org.springframework.security.saml.websso.WebSSOProfileConsumerImpl;
import org.springframework.security.saml.websso.WebSSOProfileECPImpl;
import org.springframework.security.saml.websso.WebSSOProfileImpl;
import org.springframework.security.saml.websso.WebSSOProfileOptions;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class WebSecurityConfiguration {

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

  @Configuration
  @Order(2)
  public static class ApiSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private DatabaseAuthenticationProvider dbAuthProvider;

    @Bean
    public AuthenticationEntryPoint apiAuthenticationEntryPoint() {
      BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
      entryPoint.setRealmName("api realm");
      return entryPoint;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.authenticationProvider(dbAuthProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
          .antMatcher("/api/**")
            .authorizeRequests().anyRequest().fullyAuthenticated()
            .and()
          .httpBasic()
            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
//          .authenticationEntryPoint(apiAuthenticationEntryPoint())
            .and()
          .cors()
            .and()
          .exceptionHandling()
            .and()
          .headers()
            .frameOptions().disable()
            .httpStrictTransportSecurity().disable()
            .and()
          .csrf().disable();
    }

  }

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Configuration
  @Order(3)
  @ConditionalOnProperty(name = "security.sso", havingValue = "none", matchIfMissing = true)
  public static class WebAppSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private DatabaseAuthenticationProvider dbAuthProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
      return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.authenticationProvider(dbAuthProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
          .authorizeRequests()
            .antMatchers("/static/**").permitAll()
            .antMatchers("/error").permitAll()
            .antMatchers("/login").permitAll()
            .antMatchers("/auth/**").permitAll()
           .anyRequest().fullyAuthenticated()
            .and()
          .formLogin()
            .loginPage("/login")
            .defaultSuccessUrl("/")
             .permitAll()
           .and()
          .logout()
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")
            .invalidateHttpSession(true)
            .and()
          .headers()
            .frameOptions().disable()
            .httpStrictTransportSecurity().disable()
            .and()
          .csrf().disable();
    }

  }

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Configuration
  @Order(3)
  @ConditionalOnProperty(name = "security.sso", havingValue = "okta-saml")
  public static class WebOktaSSOAppSecurityConfiguration
      extends WebSecurityConfigurerAdapter implements DisposableBean, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebOktaSSOAppSecurityConfiguration.class);

    @Value("${saml.audience}")
    private String samlAudience;

    @Value("${saml.keystore.location}")
    private String samlKeystoreLocation;

    @Value("${saml.keystore.password}")
    private String samlKeystorePassword;

    @Value("${saml.keystore.alias}")
    private String samlKeystoreAlias;

    @Value("${saml.idp}")
    private String defaultIdp;

    @Value("${saml.metadata-url}")
    private String metadataUrl;

    @Value("${saml.metadata-base-url}")
    private String metadataBaseUrl;

    @Value("${saml.max-authentication-age:86400}")
    private long maxAuthenticationAge;

    @Autowired
    private AppUserDetailsService appUserDetailsService;

    @Autowired
    private DatabaseAuthenticationProvider dbAuthProvider;

    private Timer backgroundTaskTimer;

    private MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager;

    @Bean
    @Qualifier("saml")
    public Timer getBackgroundTaskTimer() {
      return backgroundTaskTimer;
    }

    @Bean
    @Qualifier("saml")
    public MultiThreadedHttpConnectionManager getMultiThreadedHttpConnectionManager() {
      return multiThreadedHttpConnectionManager;
    }

    @Bean
    public VelocityEngine velocityEngine() {
      return VelocityFactory.getEngine();
    }

    @Bean(initMethod = "initialize")
    public StaticBasicParserPool parserPool() {
      return new StaticBasicParserPool();
    }

    @Bean(name = "parserPoolHolder")
    public ParserPoolHolder parserPoolHolder() {
      return new ParserPoolHolder();
    }

    @Bean
    public HttpClient httpClient() {
      return new HttpClient(multiThreadedHttpConnectionManager);
    }

    @Bean
    public SAMLAuthenticationProvider samlAuthenticationProvider() {
      SAMLAuthenticationProvider samlAuthenticationProvider = new SAMLAuthenticationProvider();
      samlAuthenticationProvider.setUserDetails(appUserDetailsService);
      samlAuthenticationProvider.setForcePrincipalAsString(false);
      return samlAuthenticationProvider;
    }

    @Bean
    public SAMLContextProviderImpl contextProvider() {
      return new SAMLContextProviderImpl();
    }

    @Bean
    public static SAMLBootstrap sAMLBootstrap() {
      return new SAMLBootstrap();
    }

    @Bean
    public SAMLDefaultLogger samlLogger() {
      return new SAMLDefaultLogger();
    }

    @Bean
    public WebSSOProfileConsumer webSSOprofileConsumer() {
      WebSSOProfileConsumerImpl consumer = new WebSSOProfileConsumerImpl();
      consumer.setMaxAuthenticationAge(maxAuthenticationAge);
      return consumer;
    }

    @Bean
    @Qualifier("hokWebSSOprofileConsumer")
    public WebSSOProfileConsumerHoKImpl hokWebSSOProfileConsumer() {
      return new WebSSOProfileConsumerHoKImpl();
    }

    @Bean
    public WebSSOProfile webSSOprofile() {
      return new WebSSOProfileImpl();
    }

    @Bean
    public WebSSOProfileConsumerHoKImpl hokWebSSOProfile() {
      return new WebSSOProfileConsumerHoKImpl();
    }

    @Bean
    public WebSSOProfileECPImpl ecpProfile() {
      return new WebSSOProfileECPImpl();
    }

    @Bean
    public SingleLogoutProfile logoutProfile() {
      return new SingleLogoutProfileImpl();
    }

    @Bean
    public KeyManager keyManager() {
      DefaultResourceLoader loader = new DefaultResourceLoader();
      Resource storeFile = loader.getResource(samlKeystoreLocation);
      Map<String, String> passwords = new HashMap<>();
      passwords.put(samlKeystoreAlias, samlKeystorePassword);
      return new JKSKeyManager(storeFile, samlKeystorePassword, passwords, samlKeystoreAlias);
    }

    @Bean
    public WebSSOProfileOptions defaultWebSSOProfileOptions() {
      WebSSOProfileOptions webSSOProfileOptions = new WebSSOProfileOptions();
      webSSOProfileOptions.setIncludeScoping(false);
      return webSSOProfileOptions;
    }

    @Bean
    public SAMLEntryPoint samlEntryPoint() {
      SAMLEntryPoint samlEntryPoint = new SAMLEntryPoint();
      samlEntryPoint.setDefaultProfileOptions(defaultWebSSOProfileOptions());
      return samlEntryPoint;
    }

    @Bean
    public ExtendedMetadata extendedMetadata() {
      ExtendedMetadata extendedMetadata = new ExtendedMetadata();
      extendedMetadata.setIdpDiscoveryEnabled(true);
      extendedMetadata.setSigningAlgorithm("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
      extendedMetadata.setSignMetadata(true);
      extendedMetadata.setEcpEnabled(true);
      return extendedMetadata;
    }

    @Bean
    public SAMLDiscovery samlIDPDiscovery() {
      SAMLDiscovery idpDiscovery = new SAMLDiscovery();
      idpDiscovery.setIdpSelectionPath("/saml/discovery");
      return idpDiscovery;
    }

    @Bean
    @Qualifier("okta")
    public ExtendedMetadataDelegate oktaExtendedMetadataProvider(Timer backgroundTaskTimer) throws MetadataProviderException {
      HTTPMetadataProvider metadataProvider
          = new HTTPMetadataProvider(backgroundTaskTimer, httpClient(), metadataUrl);
      metadataProvider.setParserPool(parserPool());
      metadataProvider.initialize();

      ExtendedMetadataDelegate extendedMetadataDelegate =
          new ExtendedMetadataDelegate(metadataProvider, extendedMetadata());
      extendedMetadataDelegate.setMetadataTrustCheck(true);
      extendedMetadataDelegate.setMetadataRequireSignature(false);

      backgroundTaskTimer.purge();
      return extendedMetadataDelegate;
    }

    @Bean
    @Qualifier("metadata")
    public CachingMetadataManager metadata(ExtendedMetadataDelegate oktaExtendedMetadataProvider) throws MetadataProviderException, ResourceException {
      List<MetadataProvider> providers = new ArrayList<>();
      providers.add(oktaExtendedMetadataProvider);
      CachingMetadataManager metadataManager = new CachingMetadataManager(providers);
      metadataManager.setDefaultIDP(defaultIdp);
      return metadataManager;
    }

    @Bean
    public MetadataDisplayFilter metadataDisplayFilter() {
      return new MetadataDisplayFilter();
    }

    @Bean
    public SimpleUrlLogoutSuccessHandler successLogoutHandler() {
      SimpleUrlLogoutSuccessHandler successLogoutHandler = new SimpleUrlLogoutSuccessHandler();
      successLogoutHandler.setDefaultTargetUrl("/");
      return successLogoutHandler;
    }

    @Bean
    public SecurityContextLogoutHandler logoutHandler() {
      SecurityContextLogoutHandler logoutHandler =
          new SecurityContextLogoutHandler();
      logoutHandler.setInvalidateHttpSession(true);
      logoutHandler.setClearAuthentication(true);
      return logoutHandler;
    }

    @Bean
    public SAMLLogoutProcessingFilter samlLogoutProcessingFilter() {
      return new SAMLLogoutProcessingFilter(successLogoutHandler(),
          logoutHandler());
    }

    @Bean
    public SAMLLogoutFilter samlLogoutFilter() {
      return new SAMLLogoutFilter(successLogoutHandler(),
          new LogoutHandler[] { logoutHandler() },
          new LogoutHandler[] { logoutHandler() });
    }

    private ArtifactResolutionProfile artifactResolutionProfile() {
      final ArtifactResolutionProfileImpl artifactResolutionProfile =
          new ArtifactResolutionProfileImpl(httpClient());
      artifactResolutionProfile.setProcessor(new SAMLProcessorImpl(soapBinding()));
      return artifactResolutionProfile;
    }

    @Bean
    public HTTPArtifactBinding artifactBinding(ParserPool parserPool, VelocityEngine velocityEngine) {
      return new HTTPArtifactBinding(parserPool, velocityEngine, artifactResolutionProfile());
    }

    @Bean
    public HTTPSOAP11Binding soapBinding() {
      return new HTTPSOAP11Binding(parserPool());
    }

    @Bean
    public HTTPPostBinding httpPostBinding() {
      return new HTTPPostBinding(parserPool(), velocityEngine());
    }

    @Bean
    public HTTPRedirectDeflateBinding httpRedirectDeflateBinding() {
      return new HTTPRedirectDeflateBinding(parserPool());
    }

    @Bean
    public HTTPSOAP11Binding httpSOAP11Binding() {
      return new HTTPSOAP11Binding(parserPool());
    }

    @Bean
    public HTTPPAOS11Binding httpPAOS11Binding() {
      return new HTTPPAOS11Binding(parserPool());
    }

    // Processor
    @Bean
    public SAMLProcessorImpl processor() {
      Collection<SAMLBinding> bindings = new ArrayList<>();
      bindings.add(httpRedirectDeflateBinding());
      bindings.add(httpPostBinding());
      bindings.add(artifactBinding(parserPool(), velocityEngine()));
      bindings.add(httpSOAP11Binding());
      bindings.add(httpPAOS11Binding());
      return new SAMLProcessorImpl(bindings);
    }

    public MetadataGenerator metadataGenerator() {
      MetadataGenerator metadataGenerator = new MetadataGenerator();
      metadataGenerator.setEntityId(samlAudience);
      metadataGenerator.setExtendedMetadata(extendedMetadata());
      metadataGenerator.setIncludeDiscoveryExtension(false);
      metadataGenerator.setKeyManager(keyManager());
      metadataGenerator.setEntityBaseURL(metadataBaseUrl);
      return metadataGenerator;
    }

    // Handler deciding where to redirect user after successful login
    @Bean
    @Qualifier("saml")
    public SavedRequestAwareAuthenticationSuccessHandler samlAuthSuccessHandler() {
      SavedRequestAwareAuthenticationSuccessHandler successRedirectHandler =
          new SavedRequestAwareAuthenticationSuccessHandler();
      successRedirectHandler.setDefaultTargetUrl("/");
      return successRedirectHandler;
    }

    // Handler deciding where to redirect user after failed login
    @Bean
    @Qualifier("saml")
    public SimpleUrlAuthenticationFailureHandler samlAuthFailureHandler() {
      SimpleUrlAuthenticationFailureHandler failureHandler =
          new SimpleUrlAuthenticationFailureHandler();
      failureHandler.setUseForward(true);
      failureHandler.setDefaultFailureUrl("/error");
      return failureHandler;
    }

    @Bean
    public MetadataGeneratorFilter metadataGeneratorFilter() {
      return new MetadataGeneratorFilter(metadataGenerator());
    }

    @Bean
    public SAMLWebSSOHoKProcessingFilter samlWebSSOHoKProcessingFilter() throws Exception {
      SAMLWebSSOHoKProcessingFilter samlWebSSOHoKProcessingFilter = new SAMLWebSSOHoKProcessingFilter();
      samlWebSSOHoKProcessingFilter.setAuthenticationSuccessHandler(samlAuthSuccessHandler());
      samlWebSSOHoKProcessingFilter.setAuthenticationManager(authenticationManager());
      samlWebSSOHoKProcessingFilter.setAuthenticationFailureHandler(samlAuthFailureHandler());
      return samlWebSSOHoKProcessingFilter;
    }

    // Processing filter for WebSSO profile messages
    @Bean
    public SAMLProcessingFilter samlWebSSOProcessingFilter() throws Exception {
      SAMLProcessingFilter samlWebSSOProcessingFilter = new SAMLProcessingFilter();
      samlWebSSOProcessingFilter.setAuthenticationManager(authenticationManager());
      samlWebSSOProcessingFilter.setAuthenticationSuccessHandler(samlAuthSuccessHandler());
      samlWebSSOProcessingFilter.setAuthenticationFailureHandler(samlAuthFailureHandler());
      return samlWebSSOProcessingFilter;
    }

    /**
     * Define the security filter chain in order to support SSO Auth by using SAML 2.0
     *
     * @return Filter chain proxy
     */
    @Bean
    public FilterChainProxy samlFilter() throws Exception {
      List<SecurityFilterChain> chains = new ArrayList<>();
      chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/login/**"),
          samlEntryPoint()));
      chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/logout/**"),
          samlLogoutFilter()));
      chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/metadata/**"),
          metadataDisplayFilter()));
      chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SSO/**"),
          samlWebSSOProcessingFilter()));
      chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SSOHoK/**"),
          samlWebSSOHoKProcessingFilter()));
      chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SingleLogout/**"),
          samlLogoutProcessingFilter()));
      chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/discovery/**"),
          samlIDPDiscovery()));
      return new FilterChainProxy(chains);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
      return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.authenticationProvider(dbAuthProvider);
      auth.authenticationProvider(samlAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
          .authorizeRequests()
          .antMatchers("/static/**").permitAll()
          .antMatchers("/error").permitAll()
          .antMatchers("/login").permitAll()
          .antMatchers("/saml/**").permitAll()
          .antMatchers("/auth/**").permitAll()
          .anyRequest().fullyAuthenticated();

      http
          .httpBasic()
          .authenticationEntryPoint((request, response, exception) -> {
            if (request.getRequestURI().endsWith("doSaml")) {
              samlEntryPoint().commence(request, response, exception);
            } else {
              response.sendRedirect("/login");
            }
          });

      http
          .addFilterBefore(metadataGeneratorFilter(), ChannelProcessingFilter.class)
          .addFilterAfter(samlFilter(), BasicAuthenticationFilter.class)
          .addFilterBefore(samlFilter(), CsrfFilter.class);

      http
          .logout()
          .addLogoutHandler((request, response, authentication) -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth instanceof AppUserDetails) {
              AppUserDetails userDetails = (AppUserDetails) auth.getPrincipal();
              if (userDetails.getAuthMethod() == AuthMethod.SAML) {
                try {
                  response.sendRedirect("/saml/logout");
                } catch (Exception e) {
                  LOGGER.error("Error processing logout for SAML user", e);
                  throw new RuntimeException(e);
                }
              }
            }
          });

      http
          .headers()
          .frameOptions().disable()
          .httpStrictTransportSecurity().disable();

      http
          .csrf().disable();
    }

    @Override
    public void afterPropertiesSet() {
      this.backgroundTaskTimer = new Timer(true);
      this.multiThreadedHttpConnectionManager = new MultiThreadedHttpConnectionManager();
    }

    @Override
    public void destroy() throws Exception {
      this.backgroundTaskTimer.purge();
      this.backgroundTaskTimer.cancel();
      this.multiThreadedHttpConnectionManager.shutdown();
    }

  }

}
