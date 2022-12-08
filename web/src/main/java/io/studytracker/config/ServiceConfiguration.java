/*
 * Copyright 2022 the original author or authors.
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

import io.studytracker.config.properties.EmailProperties;
import io.studytracker.service.NamingService;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
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

  @ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${email.host:}')")
  @Configuration
  public static class MailServiceConfiguration {

    @Autowired
    private EmailProperties emailProperties;

    @Bean
    public JavaMailSender javaMailSender() {
      JavaMailSenderImpl sender = new JavaMailSenderImpl();
      Properties props = sender.getJavaMailProperties();
      props.put("mail.transport.protocol", emailProperties.getProtocol());
      sender.setHost(emailProperties.getHost());
      sender.setPort(emailProperties.getPort());
      if (emailProperties.getSmtpAuth()) {
        sender.setUsername(emailProperties.getUsername());
        sender.setPassword(emailProperties.getPassword());
        props.put("mail.smtp.auth", emailProperties.getSmtpAuth());
        props.put("mail.smtp.starttls.enable", emailProperties.getSmtpStartTls());
      }
      return sender;
    }

  }

}
