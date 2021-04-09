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
import com.decibeltx.studytracker.security.DatabaseAuthenticationProvider;
import com.decibeltx.studytracker.security.UserAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
  public static class WebAppSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private DatabaseAuthenticationProvider dbAuthProvider;

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
           .anyRequest().fullyAuthenticated()
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
