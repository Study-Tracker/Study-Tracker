package com.decibeltx.studytracker.config;

import com.decibeltx.studytracker.benchling.api.BenchlingElnRestClient;
import com.decibeltx.studytracker.benchling.api.BenchlingNotebookService;
import com.decibeltx.studytracker.benchling.exception.BenchlingExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
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

//    private static final Logger LOGGER = LoggerFactory.getLogger(BenchlingElnServiceConfiguration.class);

//    @Autowired
//    private Environment env;

    @Bean
    public ObjectMapper BenchlingElnObjectMapper() {
      return new ObjectMapper();
    }

    @Bean(name = "benchlingElnRestTemplate")
    public RestTemplate BenchlingElnRestTemplate() {
      RestTemplate restTemplate = new RestTemplateBuilder()
          .errorHandler(new BenchlingExceptionHandler(BenchlingElnObjectMapper()))
          .build();
      MappingJackson2HttpMessageConverter httpMessageConverter = new MappingJackson2HttpMessageConverter();
      httpMessageConverter.setObjectMapper(BenchlingElnObjectMapper());
      restTemplate.getMessageConverters().add(0, httpMessageConverter);
      SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
      requestFactory.setOutputStreaming(false);
      restTemplate.setRequestFactory(requestFactory);
      return restTemplate;
    }

//    @Bean
//    public BenchlingElnOptions elnOptions() throws Exception {
//
//      LOGGER.info("Configuring Benchling integration...");
//      BenchlingElnOptions options = new BenchlingElnOptions();
//
//      // Authentication
//      if (env.containsProperty("benchling.eln.api.token")) {
//        Assert.notNull(env.getRequiredProperty("benchling.eln.api.token"),
//            "API token must not be null. Eg. benchling.eln.api.token=xxx");
//        options.setApiToken(env.getRequiredProperty("benchling.eln.api.token"));
//      } else if (env.containsProperty("benchling.eln.api.username") && env
//          .containsProperty("benchling.eln.api.password")) {
//        Assert.notNull(env.getRequiredProperty("benchling.eln.api.username"),
//            "API username must not be null. Eg. benchling.eln.api.username=xxx");
//        Assert.notNull(env.getRequiredProperty("benchling.eln.api.password"),
//            "API password must not be null. Eg. benchling.eln.api.password=xxx");
//        options.setUsername(env.getRequiredProperty("benchling.eln.api.username"));
//        options.setPassword(env.getRequiredProperty("benchling.eln.api.password"));
//      } else {
//        throw new BenchlingException(
//            "Missing configuration properties. Authentication requires the 'benchling.eln.api.username' and 'benchling.eln.api.password' properties or the 'benchling.eln.api.token' property.");
//      }
//
//      Assert.notNull(env.getProperty("benchling.eln.api.root-url"),
//          "benchling ELN API root URL is not set.");
//      options.setRootUrl(new URL(env.getRequiredProperty("benchling.eln.api.root-url")));
//      Assert.notNull(env.getProperty("benchling.eln.api.root-entity"),
//          "benchling ELN API root entity is not set.");
//      options.setRootEntity(env.getRequiredProperty("benchling.eln.api.root-entity"));
//
//      //Folder URL
//      Assert.notNull(env.getProperty("benchling.eln.api.root-folder-url"),
//          "benchling ELN API root entity is not set.");
//      options.setRootFolderUrl(env.getRequiredProperty("benchling.eln.api.root-folder-url"));
//
//      return options;
//    }

    @Bean
    public BenchlingElnRestClient BenchlingRestElnClient() throws Exception {
      return new BenchlingElnRestClient();
    }

    @Bean
    public BenchlingNotebookService benchlingNotebookService() {
      return new BenchlingNotebookService();
    }

  }

}
