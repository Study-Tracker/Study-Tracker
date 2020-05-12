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

package com.decibeltx.studytracker.idbs.inventory;

import com.decibeltx.studytracker.idbs.exception.IdbsExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnProperty(name = "inventory.mode", havingValue = "idbs")
public class IdbsInventoryServiceConfiguration {

  @Autowired
  private Environment env;

  @Bean
  public ObjectMapper idbsInventoryObjectMapper() {
    return new ObjectMapper();
  }

  @Bean
  public RestTemplate idbsInventoryRestTemplate() {
    RestTemplate restTemplate = new RestTemplateBuilder()
        .errorHandler(new IdbsExceptionHandler(idbsInventoryObjectMapper()))
        .build();
    MappingJackson2HttpMessageConverter httpMessageConverter = new MappingJackson2HttpMessageConverter();
    httpMessageConverter.setObjectMapper(idbsInventoryObjectMapper());
    restTemplate.getMessageConverters().add(0, httpMessageConverter);
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setOutputStreaming(false);
    restTemplate.setRequestFactory(requestFactory);
    return restTemplate;
  }

  @Bean
  public InventoryRestApiAuthenticationService authenticationService() throws Exception {
    Assert.notNull(env.getProperty("idbs.inventory.api.root-url"),
        "IDBS Inventory API root URL is not set.");
    Assert.notNull(env.getProperty("idbs.inventory.api.username"),
        "IDBS Inventory API username not set.");
    Assert.notNull(env.getProperty("idbs.inventory.api.password"),
        "IDBS Inventory API password is not set.");
    return new InventoryRestApiAuthenticationService(idbsInventoryRestTemplate(),
        new URL(env.getRequiredProperty("idbs.inventory.api.root-url")),
        env.getProperty("idbs.inventory.api.username"),
        env.getProperty("idbs.inventory.api.password")
    );
  }

  @Bean
  public IdbsInventoryService idbsInventoryService() throws Exception {
    Assert.notNull(env.getProperty("idbs.inventory.api.root-url"),
        "IDBS Inventory API root URL is not set.");
    return new IdbsRestApiInventoryService(idbsInventoryRestTemplate(),
        authenticationService(),
        new URL(env.getRequiredProperty("idbs.inventory.api.root-url"))
    );
  }

}
