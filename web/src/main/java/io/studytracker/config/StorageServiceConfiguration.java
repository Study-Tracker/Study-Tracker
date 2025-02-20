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

import io.studytracker.storage.LocalFileSystemStorageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageServiceConfiguration {

  @Configuration
  @ConditionalOnProperty(name = "storage.mode", havingValue = "local", matchIfMissing = true)
  public static class LocalStudyStorageServiceConfiguration {

    @Bean
    public LocalFileSystemStorageService localFileSystemStudyStorageService() {
      return new LocalFileSystemStorageService();
    }
  }

//  @Configuration
//  @ConditionalOnProperty(name = "storage.mode", havingValue = "egnyte")
//  @AutoConfigureBefore(StorageServiceConfiguration.class)
//  public static class EgnyteServiceConfiguration {
//
//    @Bean
//    public ObjectMapper egnyteObjectMapper(EgnyteProperties egnyteProperties) throws Exception {
//      String url;
//      if (StringUtils.hasText(egnyteProperties.getRootUrl())) {
//        url = egnyteProperties.getRootUrl();
//      } else if (StringUtils.hasText(egnyteProperties.getTenantName())) {
//        url = "https://" + egnyteProperties.getTenantName() + ".egnyte.com";
//      } else {
//        throw new InvalidConfigurationException(
//            "Egnyte root URL or tenant name is not set. Eg. egnyte.root-url=https://tenant.egnyte.com");
//      }
//      ObjectMapper objectMapper = new ObjectMapper();
//      objectMapper.registerModule(
//          new SimpleModule() {
//            {
//              addDeserializer(
//                  EgnyteObject.class,
//                  new EgnyteObjectDeserializer(new URL(url)));
//            }
//          });
//      return objectMapper;
//    }
//
//    @Bean
//    public RestTemplate egnyteRestTemplate(EgnyteProperties egnyteProperties) throws Exception {
//      RestTemplate restTemplate = new RestTemplateBuilder().build();
//      MappingJackson2HttpMessageConverter httpMessageConverter =
//          new MappingJackson2HttpMessageConverter();
//      httpMessageConverter.setObjectMapper(egnyteObjectMapper(egnyteProperties));
//      restTemplate.getMessageConverters().add(0, httpMessageConverter);
//      return restTemplate;
//    }
//
//    @Bean
//    public EgnyteRestApiClient egnyteClient(EgnyteProperties egnyteProperties)
//        throws Exception {
//      return new EgnyteRestApiClient(egnyteRestTemplate(egnyteProperties));
//    }
//
//    @Bean
//    public EgnyteStudyStorageService egnyteStorageService() {
//      return new EgnyteStudyStorageService();
//    }
//
//  }


}
