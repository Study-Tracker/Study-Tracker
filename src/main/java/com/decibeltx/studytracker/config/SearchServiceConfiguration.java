package com.decibeltx.studytracker.config;

import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.repository.StudyRepository;
import com.decibeltx.studytracker.search.SearchService;
import com.decibeltx.studytracker.search.elasticsearch.ElasticsearchSearchService;
import java.util.Calendar;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.ClientConfiguration.ClientConfigurationBuilderWithRequiredEndpoint;
import org.springframework.data.elasticsearch.client.ClientConfiguration.MaybeSecureClientConfigurationBuilder;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@ConditionalOnProperty("search.mode")
public class SearchServiceConfiguration {

  @Configuration
  @EnableElasticsearchRepositories(basePackages = "com.decibeltx.studytracker.search.elasticsearch")
  @ConditionalOnProperty(name = "search.mode", havingValue = "elasticsearch")
  public static class ElasticsearchConfiguration {

    @Value("${elasticsearch.host}")
    private String host;

    @Value("${elasticsearch.use-ssl:#{false}}")
    private Boolean useSsl;

    @Value("${elasticsearch.username:#{null}}")
    private String username;

    @Value("${elasticsearch.password:#{null}}")
    private String password;

    @Bean
    public RestHighLevelClient client() {
      ClientConfigurationBuilderWithRequiredEndpoint builder = ClientConfiguration.builder();
      MaybeSecureClientConfigurationBuilder sBuilder = builder.connectedTo(host);
      ClientConfiguration configuration;
      if (useSsl != null && useSsl && username != null && password != null) {
        configuration = sBuilder.usingSsl()
            .withBasicAuth(username, password)
            .build();
      } else if (useSsl != null && useSsl) {
        configuration = sBuilder.usingSsl()
            .build();
      } else if (username != null && password != null) {
        configuration = sBuilder.withBasicAuth(username, password)
            .build();
      } else {
        configuration = sBuilder.build();
      }
      return RestClients.create(configuration).rest();
    }

    @Bean
    public ElasticsearchSearchService elasticsearchSearchService(){
      return new ElasticsearchSearchService();
    }

  }

  @Configuration
  @ConditionalOnProperty(name = "search.mode")
  @EnableScheduling
  public static class ScheduledSearchIndexingConfiguration {

    public static final Logger LOGGER = LoggerFactory.getLogger(SearchServiceConfiguration.class);

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private SearchService<?, ?> searchService;

    /**
     * Indexes all studies that have been updated within the past two hours. Runs on a schedule
     * every other hour.
     */
    @Scheduled(cron = "0 0 */2 * * *")
    public void scheduledStudyIndex() {
      LOGGER.info("Running scheduled study search indexing...");
      int count = 0;
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.MINUTE, -130);
      for (Study study : studyRepository.findByUpdatedAtAfter(calendar.getTime())) {
        searchService.indexStudy(study);
        count = count + 1;
      }
      LOGGER.info("Study indexing complete. Indexed {} studies", count);
    }

  }

}
