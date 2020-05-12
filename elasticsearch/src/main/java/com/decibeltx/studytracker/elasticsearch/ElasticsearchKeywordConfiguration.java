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

package com.decibeltx.studytracker.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.net.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnProperty(name = "keywords.mode", havingValue = "elasticsearch")
public class ElasticsearchKeywordConfiguration {

  @Autowired
  private Environment env;

  @Bean
  public ObjectMapper esObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addDeserializer(KeywordHits.class, new ElasticsearchKeywordDeserializer());
    objectMapper.registerModule(module);
    return objectMapper;
  }

  @Bean
  public RestTemplate esRestTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    MappingJackson2CborHttpMessageConverter converter = new MappingJackson2CborHttpMessageConverter();
    converter.setObjectMapper(esObjectMapper());
    restTemplate.getMessageConverters().add(0, converter);
    return restTemplate;
  }

  @Bean
  public ElasticsearchKeywordService elasticsearchKeywordService() throws Exception {
    Assert.notNull(env.getProperty("keywords.elasticsearch.root-url"),
        "Elasticsearch root URL must be set: keywords.elasticsearch.root-url=xxx");
    return new ElasticsearchKeywordService(
        new URL(env.getRequiredProperty("keywords.elasticsearch.root-url")),
        esRestTemplate()
    );
  }

}
