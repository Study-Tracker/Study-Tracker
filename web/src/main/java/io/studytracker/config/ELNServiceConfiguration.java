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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.studytracker.benchling.BenchlingClientFactory;
import io.studytracker.benchling.exception.BenchlingExceptionHandler;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ELNServiceConfiguration {

  @Bean
  public ObjectMapper benchlingElnObjectMapper() {
    return new ObjectMapper();
  }

  @Bean(name = "benchlingElnRestTemplate")
  public RestTemplate benchlingElnRestTemplate() {
    RestTemplate restTemplate =
        new RestTemplateBuilder()
            .errorHandler(new BenchlingExceptionHandler(benchlingElnObjectMapper()))
            .build();
    MappingJackson2HttpMessageConverter httpMessageConverter =
        new MappingJackson2HttpMessageConverter();
    httpMessageConverter.setObjectMapper(benchlingElnObjectMapper());
    restTemplate.getMessageConverters().add(0, httpMessageConverter);
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    restTemplate.setRequestFactory(requestFactory);
    return restTemplate;
  }

  @Bean
  public BenchlingClientFactory benchlingClientFactory() {
    return new BenchlingClientFactory(benchlingElnRestTemplate());
  }

}
