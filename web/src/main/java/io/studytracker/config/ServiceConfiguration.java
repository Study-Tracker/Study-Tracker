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

import io.studytracker.service.NamingService;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class ServiceConfiguration {

  @Bean
  @Primary
  public NamingService namingService() {
    return new NamingService();
  }

  @ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${spring.mail.host:}')")
  @Configuration
  public static class MailServiceConfiguration {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private Integer port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.protocol}")
    private String protocol;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private Boolean smtpAuth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private Boolean startTls;

    @Bean
    public JavaMailSender javaMailSender() {
      JavaMailSenderImpl sender = new JavaMailSenderImpl();
      Properties props = sender.getJavaMailProperties();
      props.put("mail.transport.protocol", protocol);
      sender.setHost(host);
      sender.setPort(port);
      if (smtpAuth) {
        sender.setUsername(username);
        sender.setPassword(password);
        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.smtp.starttls.enable", startTls);
      }
      return sender;
    }

  }

}
