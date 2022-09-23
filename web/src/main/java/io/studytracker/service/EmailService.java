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

package io.studytracker.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

  private JavaMailSender mailSender;

  @Value("#{servletContext.contextPath}")
  private String contextPath;

  @Value("${email.outgoing-email-address:#{null}}")
  private String outgoingEmail;

  @Value("${server.port:8080}")
  private Integer port;

  @Value("${application.host-name:localhost}")
  private String host;

  private String getRootUrl() {
    String protocol = port.equals(443) || port.equals(8443) ? "https" : "http";
    return protocol + "://" + host + ":" + port + "/" + contextPath;
  }

  public void sendPasswordResetEmail(String emailAddress, String token) {

    if (mailSender == null) {
      LOGGER.warn(
          "Mail service is not configured. Check config properties and restart the application if necessary.");
      return;
    }

    String text =
        "You are receiving this email because a password reset request has been made for "
            + "your account. To reset your password, click the link below and follow the provided "
            + "instructions:\n\n"
            + getRootUrl()
            + "auth/passwordreset?token="
            + URLEncoder.encode(token, StandardCharsets.UTF_8)
            + "&email="
            + URLEncoder.encode(emailAddress, StandardCharsets.UTF_8);

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(emailAddress);
    message.setFrom(outgoingEmail);
    message.setSubject("Study Tracker: Password reset request");
    message.setText(text);
    mailSender.send(message);
  }

  public void sendNewUserEmail(String emailAddress, String token) {

    if (mailSender == null) {
      LOGGER.warn(
          "Mail service is not configured. Check config properties and restart the application if necessary.");
      return;
    }

    String text =
        "Welcome to Study Tracker! To activate your account, click on the link below to "
            + "confirm your registration and set a new password for your account.\n\n"
            + getRootUrl()
            + "auth/passwordreset?token="
            + URLEncoder.encode(token, StandardCharsets.UTF_8)
            + "&email="
            + URLEncoder.encode(emailAddress, StandardCharsets.UTF_8);

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(emailAddress);
    message.setFrom(outgoingEmail);
    message.setSubject("Welcome to Study Tracker");
    message.setText(text);
    mailSender.send(message);
  }

  @Autowired(required = false)
  public void setMailSender(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }
}
