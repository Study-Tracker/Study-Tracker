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

import io.studytracker.config.properties.OpensearchProperties;
import io.studytracker.model.Assay;
import io.studytracker.model.Study;
import io.studytracker.repository.AssayRepository;
import io.studytracker.repository.StudyRepository;
import io.studytracker.search.SearchService;
import io.studytracker.search.opensearch.OpensearchSearchService;
import java.util.Calendar;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.JsonpMapper;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.TransportOptions;
import org.opensearch.client.transport.rest_client.RestClientOptions;
import org.opensearch.data.client.osc.OpenSearchClients;
import org.opensearch.data.client.osc.OpenSearchTemplate;
import org.opensearch.data.core.OpenSearchOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.ClientConfiguration.ClientConfigurationBuilderWithRequiredEndpoint;
import org.springframework.data.elasticsearch.client.ClientConfiguration.MaybeSecureClientConfigurationBuilder;
import org.springframework.data.elasticsearch.config.ElasticsearchConfigurationSupport;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@ConditionalOnProperty("search.mode")
public class SearchServiceConfiguration {

  @Configuration
  @EnableElasticsearchRepositories(basePackages = "io.studytracker.search.opensearch")
  @ConditionalOnProperty(name = "search.mode", havingValue = "opensearch")
  public static class OpensearchSearchServiceConfiguration
      extends ElasticsearchConfigurationSupport {

    @Autowired
    private OpensearchProperties properties;

    @Bean
    public JsonpMapper jsonpMapper() {
      return new JacksonJsonpMapper();
    }

    public TransportOptions transportOptions() {
      return new RestClientOptions(RequestOptions.DEFAULT);
    }

    @Bean
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
    public RestClient restClient(ClientConfiguration clientConfiguration) {
      return OpenSearchClients.getRestClient(clientConfiguration);
    }

    @Bean
    public OpenSearchTransport opensearchTransport(RestClient restClient, JsonpMapper jsonpMapper) {
      return OpenSearchClients.getOpenSearchTransport(restClient, OpenSearchClients.IMPERATIVE_CLIENT,
          transportOptions(), jsonpMapper());
    }

    @Bean
    public OpenSearchClient opensearchClient(OpenSearchTransport transport) {
      return OpenSearchClients.createImperative(transport);
    }

    @Bean(name = "elasticsearchTemplate")
    public OpenSearchOperations opensearchOperations(ElasticsearchConverter elasticsearchConverter,
        OpenSearchClient elasticsearchClient) {
      OpenSearchTemplate template = new OpenSearchTemplate(elasticsearchClient, elasticsearchConverter);
      template.setRefreshPolicy(this.refreshPolicy());
      return template;
    }

    @Bean
    public OpensearchSearchService opensearchSearchService() {
      return new OpensearchSearchService();
    }
  }

  @Configuration
  @ConditionalOnProperty(name = "search.mode", havingValue = "opensearch")
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
