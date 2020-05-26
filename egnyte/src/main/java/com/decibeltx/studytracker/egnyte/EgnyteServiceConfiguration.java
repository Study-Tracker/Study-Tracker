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

package com.decibeltx.studytracker.egnyte;

import com.decibeltx.studytracker.core.config.LocalStudyStorageServiceConfiguration;
import com.decibeltx.studytracker.egnyte.entity.EgnyteObject;
import com.decibeltx.studytracker.egnyte.rest.EgnyteObjectDeserializer;
import com.decibeltx.studytracker.egnyte.rest.EgnyteRestApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.net.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnProperty(name = "storage.mode", havingValue = "egnyte")
@AutoConfigureBefore(LocalStudyStorageServiceConfiguration.class)
public class EgnyteServiceConfiguration {

  @Autowired
  private Environment env;

  @Bean
  public ObjectMapper egnyteObjectMapper() throws Exception {
    Assert.notNull(env.getProperty("egnyte.root-url"), "Egnyte root URL is not set.");
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new SimpleModule() {{
      addDeserializer(EgnyteObject.class,
          new EgnyteObjectDeserializer(new URL(env.getRequiredProperty("egnyte.root-url"))));
    }});
    return objectMapper;
  }

  @Bean
  public RestTemplate egnyteRestTemplate() throws Exception {
    RestTemplate restTemplate = new RestTemplateBuilder().build();
    MappingJackson2HttpMessageConverter httpMessageConverter = new MappingJackson2HttpMessageConverter();
    httpMessageConverter.setObjectMapper(egnyteObjectMapper());
    restTemplate.getMessageConverters().add(0, httpMessageConverter);
    return restTemplate;
  }

  @Bean
  public EgnyteOptions egnyteOptions() throws Exception {
    Assert.notNull(env.getProperty("egnyte.root-url"), "Egnyte root URL is not set.");
    Assert.notNull(env.getProperty("egnyte.root-path"), "Egnyte root directory path is not set.");
    Assert.notNull(env.getProperty("egnyte.api-token"), "Egnyte API token is not set.");
    EgnyteOptions options = new EgnyteOptions();
    options.setRootUrl(new URL(env.getRequiredProperty("egnyte.root-url")));
    options.setRootPath(env.getRequiredProperty("egnyte.root-path"));
    options.setToken(env.getRequiredProperty("egnyte.api-token"));
    if (env.containsProperty("egnyte.qps")) {
      options.setQps(env.getRequiredProperty("egnyte.qps", Integer.class));
    } else if (env.containsProperty("egnyte.sleep")) {
      options.setSleep(env.getRequiredProperty("egnyte.sleep", Integer.class));
    }
    if (env.containsProperty("storage.max-folder-read-depth")) {
      options.setMaxReadDepth(env.getRequiredProperty("storage.max-folder-read-depth", int.class));
    }
    if (env.containsProperty("storage.use-existing")) {
      options.setUseExisting(env.getRequiredProperty("storage.use-existing", boolean.class));
    }
    return options;
  }

  @Bean
  public EgnyteClientOperations egnyteClient(EgnyteOptions egnyteOptions) throws Exception {
    return new EgnyteRestApiClient(egnyteRestTemplate(), egnyteOptions);
  }

  @Bean
  public EgnyteStudyStorageService egnyteStorageService(
      EgnyteClientOperations egnyteClient, EgnyteOptions options) {
    return new EgnyteStudyStorageService(egnyteClient, options);
  }

}
