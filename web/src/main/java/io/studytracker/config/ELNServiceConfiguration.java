package io.studytracker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.studytracker.benchling.api.BenchlingElnRestClient;
import io.studytracker.benchling.api.BenchlingNotebookService;
import io.studytracker.benchling.exception.BenchlingExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ELNServiceConfiguration {

  @Configuration
  @ConditionalOnProperty(name = "notebook.mode", havingValue = "benchling")
  public static class BenchlingElnServiceConfiguration {

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
      requestFactory.setOutputStreaming(false);
      restTemplate.setRequestFactory(requestFactory);
      return restTemplate;
    }

    @Bean
    public BenchlingElnRestClient benchlingRestElnClient() throws Exception {
      return new BenchlingElnRestClient();
    }

    @Bean
    public BenchlingNotebookService benchlingNotebookService() {
      return new BenchlingNotebookService();
    }
  }
}
