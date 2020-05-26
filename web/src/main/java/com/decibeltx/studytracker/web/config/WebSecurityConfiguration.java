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

package com.decibeltx.studytracker.web.config;

import com.decibeltx.studytracker.core.config.UserServiceAuditor;
import com.decibeltx.studytracker.core.model.User;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

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

  @Configuration
  @Order(1)
  public static class DemoSecurityConfiguration {

    @Bean
    public UserDetailsService demoUserDetailsService() {
      UserDetails user =
          org.springframework.security.core.userdetails.User.builder()
              .username("demo")
              .password("password")
              .roles("USER")
              .build();
      return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public AuthenticationProvider demoAuthenticationProvider() {
      return new DemoAuthenticationProvider(demoUserDetailsService());
    }

  }

  @Configuration
  @Order(2)
  public static class ApiSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Bean
    public AuthenticationEntryPoint apiAuthenticationEntryPoint() {
      BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
      entryPoint.setRealmName("api realm");
      return entryPoint;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
          .antMatcher("/api/**")
          .authenticationProvider(authenticationProvider)
          .authorizeRequests()
          .antMatchers(HttpMethod.POST).fullyAuthenticated()
          .antMatchers(HttpMethod.PUT).fullyAuthenticated()
          .antMatchers(HttpMethod.DELETE).fullyAuthenticated()
          .anyRequest().permitAll()
          .and()
          .httpBasic()
          .authenticationEntryPoint(apiAuthenticationEntryPoint())
          .and()
          .cors()
          .and()
          .exceptionHandling()
          .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
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
  public static class WebAppSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private UserAuthenticationSuccessHandler userAuthenticationSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
          .authenticationProvider(authenticationProvider)
          .authorizeRequests()
          .antMatchers("/studies/new", "/study/*/assays/new", "study/*/edit",
              "study/*/assays/*/edit")
          .fullyAuthenticated()
          .antMatchers("/", "/study/**", "/studies")
          .permitAll()
          .anyRequest().permitAll()
          .and()
          .formLogin()
          .successHandler(userAuthenticationSuccessHandler)
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

  static class DemoAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    public DemoAuthenticationProvider(
        UserDetailsService userDetailsService) {
      this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException {
      String username = authentication.getName();
      String password = authentication.getCredentials().toString();
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);
      if (userDetails.getUsername().equals(username) && userDetails.getPassword()
          .equals(password)) {
        return new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>());
      }
      return null;
    }

    @Override
    public boolean supports(Class<?> auth) {
      return auth.equals(UsernamePasswordAuthenticationToken.class);
    }
  }

}
