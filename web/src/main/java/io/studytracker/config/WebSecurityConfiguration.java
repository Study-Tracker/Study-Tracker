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

package io.studytracker.config;

import io.studytracker.model.User;
import io.studytracker.security.AppUserDetailsService;
import io.studytracker.security.DatabaseAuthenticationProvider;
import io.studytracker.security.TokenFilter;
import io.studytracker.security.TokenUtils;
import io.studytracker.security.UserAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

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
  @Order(1)
  public static class PublicApiSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private AppUserDetailsService appUserDetailsService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private DatabaseAuthenticationProvider dbAuthProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.authenticationProvider(dbAuthProvider);
    }

    @Bean
    public TokenUtils tokenUtils() {
      return new TokenUtils();
    }

    @Bean
    public TokenFilter tokenFilter() {
      return new TokenFilter();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http.antMatcher("/api/public/**")
          .csrf().disable()
          .httpBasic().disable()
          .cors()
          .and()
          .authorizeHttpRequests()
          .anyRequest().authenticated()
          .and()
          .userDetailsService(appUserDetailsService)
          .exceptionHandling()
          .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
          .and()
          .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

      http.addFilterBefore(tokenFilter(), UsernamePasswordAuthenticationFilter.class);

    }

  }

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Configuration
  @Order(2)
  @ConditionalOnProperty(name = "security.sso", havingValue = "none", matchIfMissing = true)
  public static class WebAppSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired private DatabaseAuthenticationProvider dbAuthProvider;

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
      http.authorizeRequests()
          .antMatchers("/static/**", "/error", "/login", "/auth/**").permitAll()
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
          .exceptionHandling()
            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
          .and()
          .headers()
            .frameOptions().disable()
            .httpStrictTransportSecurity().disable()
          .and()
          .csrf()
            .ignoringAntMatchers("/auth/**")
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
      ;
    }
  }

}
