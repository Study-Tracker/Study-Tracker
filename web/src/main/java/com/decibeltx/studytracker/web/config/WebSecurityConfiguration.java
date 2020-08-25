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

import com.decibeltx.studytracker.core.config.UserRepositoryPopulator;
import com.decibeltx.studytracker.core.config.UserServiceAuditor;
import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.web.example.ExampleUserRepositoryPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
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

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(); // TODO add salt
  }

  @Configuration
  @Order(1)
  @ConditionalOnProperty(name = "security.mode", havingValue = "demo")
  public static class DemoSecurityConfiguration {

    @Bean
    public UserRepositoryPopulator exampleUserRepositoryPopulator() {
      return new ExampleUserRepositoryPopulator();
    }

  }

  @Configuration
  @Order(2)
  public static class ApiSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationEntryPoint apiAuthenticationEntryPoint() {
      BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
      entryPoint.setRealmName("api realm");
      return entryPoint;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth
          .userDetailsService(userDetailsService)
          .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
          .antMatcher("/api/**")
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

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @Autowired
//    private UserAuthenticationSuccessHandler userAuthenticationSuccessHandler;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth
          .userDetailsService(userDetailsService)
          .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
          .authorizeRequests()
          .antMatchers("/studies/new", "/study/*/assays/new", "study/*/edit",
              "study/*/assays/*/edit", "/programs/new", "/users/new")
          .fullyAuthenticated()
          .antMatchers("/", "/study/**", "/studies")
          .permitAll()
          .anyRequest()
          .permitAll()
          .and()
          .formLogin()
          .loginPage("/login")
          //.loginProcessingUrl("/authenticate")
          //.failureUrl("/login?error=true")
          //.successHandler(userAuthenticationSuccessHandler)
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

}
