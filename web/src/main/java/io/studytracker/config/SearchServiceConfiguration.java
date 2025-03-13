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

import io.studytracker.config.properties.ElasticsearchProperties;
import io.studytracker.model.Assay;
import io.studytracker.model.Study;
import io.studytracker.repository.AssayRepository;
import io.studytracker.repository.StudyRepository;
import io.studytracker.search.SearchService;
import io.studytracker.search.elasticsearch.ElasticsearchSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.ClientConfiguration.ClientConfigurationBuilderWithRequiredEndpoint;
import org.springframework.data.elasticsearch.client.ClientConfiguration.MaybeSecureClientConfigurationBuilder;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Calendar;

@Configuration
@ConditionalOnProperty("search.mode")
public class SearchServiceConfiguration {

  @Configuration
  @EnableElasticsearchRepositories(basePackages = "io.studytracker.search.elasticsearch")
  @ConditionalOnProperty(name = "search.mode", havingValue = "elasticsearch")
  public static class ElasticsearchSearchServiceConfiguration extends ElasticsearchConfiguration {

    @Autowired
    private ElasticsearchProperties properties;

//    @Value("${elasticsearch.host}")
//    private String host;
//
//    @Value("${elasticsearch.port}")
//    private Integer port;
//
//    @Value("${elasticsearch.use-ssl:#{false}}")
//    private Boolean useSsl;
//
//    @Value("${elasticsearch.username:#{null}}")
//    private String username;
//
//    @Value("${elasticsearch.password:#{null}}")
//    private String password;
    
    
    @Override
    public ClientConfiguration clientConfiguration() {

      String host = properties.getHost();
      Integer port = properties.getPort();
      Boolean useSsl = properties.getUseSsl();
      String username = properties.getUsername();
      String password = properties.getPassword();

      ClientConfigurationBuilderWithRequiredEndpoint builder = ClientConfiguration.builder();
      if (port == null && useSsl) {
        port = 443;
      }
      MaybeSecureClientConfigurationBuilder sBuilder = builder.connectedTo(host + ":" + port);
      ClientConfiguration configuration;
      if (useSsl != null && useSsl && username != null && password != null) {
        configuration = sBuilder.usingSsl().withBasicAuth(username, password).build();
      } else if (useSsl != null && useSsl) {
        configuration = sBuilder.usingSsl().build();
      } else if (username != null && password != null) {
        configuration = sBuilder.withBasicAuth(username, password).build();
      } else {
        configuration = sBuilder.build();
      }
      return configuration;
    }

    @Bean
    public ElasticsearchSearchService elasticsearchSearchService() {
      return new ElasticsearchSearchService();
    }
  }

  @Configuration
  @ConditionalOnProperty(name = "search.mode")
  @EnableScheduling
  public static class ScheduledSearchIndexingConfiguration {

    public static final Logger LOGGER = LoggerFactory.getLogger(SearchServiceConfiguration.class);

    @Autowired private StudyRepository studyRepository;

    @Autowired private AssayRepository assayRepository;

    @Autowired private SearchService searchService;

    /**
     * Indexes all studies that have been updated within the past two hours. Runs on a schedule
     * every other hour.
     */
    @Scheduled(cron = "0 0 */2 * * *")
    public void scheduledDocumentIndex() {
      LOGGER.info("Running scheduled study search indexing...");

      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.MINUTE, -130);

      // Index studies
      int studyCount = 0;
      for (Study study : studyRepository.findByUpdatedAtAfter(calendar.getTime())) {
        searchService.indexStudy(study);
        studyCount = studyCount + 1;
      }

      // Index assays
      int assayCount = 0;
      for (Assay assay : assayRepository.findByUpdatedAtAfter(calendar.getTime())) {
        searchService.indexAssay(assay);
        assayCount = assayCount + 1;
      }

      LOGGER.info("Document indexing complete. Indexed {} studies and {} assays",
          studyCount, assayCount);
    }
  }
}
