/*
 * Copyright 2022 the original author or authors.
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
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.studytracker.aws.S3DataFileStorageService;
import io.studytracker.aws.S3StudyFileStorageService;
import io.studytracker.egnyte.EgnyteApiDataFileStorageService;
import io.studytracker.egnyte.EgnyteFolderNamingService;
import io.studytracker.egnyte.EgnyteStudyStorageService;
import io.studytracker.egnyte.entity.EgnyteObject;
import io.studytracker.egnyte.rest.EgnyteObjectDeserializer;
import io.studytracker.egnyte.rest.EgnyteRestApiClient;
import io.studytracker.exception.InvalidConfigurationException;
import io.studytracker.service.NamingOptions;
import io.studytracker.storage.LocalFileSystemStudyStorageService;
import io.studytracker.storage.StudyStorageService;
import java.net.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

@Configuration
public class StorageServiceConfiguration {

  @Configuration
  @ConditionalOnProperty(name = "storage.mode", havingValue = "local", matchIfMissing = true)
  public static class LocalStudyStorageServiceConfiguration {

    @Bean
    public StudyStorageService localFileSystemStudyStorageService(Environment env) {
      Assert.notNull(
          env.getProperty("storage.local-dir"),
          "Local storage directory is not set. Eg. storage.local-dir=/path/to/storage");
      return new LocalFileSystemStudyStorageService();
    }
  }

  @Configuration
  @ConditionalOnProperty(name = "storage.mode", havingValue = "egnyte")
  @AutoConfigureBefore(StorageServiceConfiguration.class)
  public static class EgnyteServiceConfiguration {

    @Bean
    public EgnyteFolderNamingService egnyteFolderNamingService(Environment env) {
      NamingOptions namingOptions = new NamingOptions();
      if (env.containsProperty("study.study-code-counter-start")) {
        namingOptions.setStudyCodeCounterStart(
            env.getRequiredProperty("study.study-code-counter-start", Integer.class));
      }
      if (env.containsProperty("study.external-code-counter-start")) {
        namingOptions.setExternalStudyCodeCounterStart(
            env.getRequiredProperty("study.external-code-counter-start", Integer.class));
      }
      if (env.containsProperty("study.assay-code-counter-start")) {
        namingOptions.setAssayCodeCounterStart(
            env.getRequiredProperty("study.assay-code-counter-start", Integer.class));
      }
      return new EgnyteFolderNamingService(namingOptions);
    }

    @Bean
    public ObjectMapper egnyteObjectMapper(Environment env) throws Exception {
      String url;
      if (env.containsProperty("egnyte.root-url")) {
        url = env.getRequiredProperty("egnyte.root-url");
      } else if (env.containsProperty("egnyte.tenant-name")) {
        url = "https://" + env.getRequiredProperty("egnyte.tenant-name") + ".egnyte.com";
      } else {
        throw new InvalidConfigurationException(
            "Egnyte root URL or tenant name is not set. Eg. egnyte.root-url=https://tenant.egnyte.com");
      }
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(
          new SimpleModule() {
            {
              addDeserializer(
                  EgnyteObject.class,
                  new EgnyteObjectDeserializer(new URL(url)));
            }
          });
      return objectMapper;
    }

    @Bean
    public RestTemplate egnyteRestTemplate(Environment env) throws Exception {
      RestTemplate restTemplate = new RestTemplateBuilder().build();
      MappingJackson2HttpMessageConverter httpMessageConverter =
          new MappingJackson2HttpMessageConverter();
      httpMessageConverter.setObjectMapper(egnyteObjectMapper(env));
      restTemplate.getMessageConverters().add(0, httpMessageConverter);
      return restTemplate;
    }

//    @Bean
//    public EgnyteOptions egnyteOptions(Environment env) throws Exception {
//      Assert.notNull(env.getProperty("egnyte.root-url"), "Egnyte root URL is not set.");
//      Assert.notNull(env.getProperty("egnyte.root-path"), "Egnyte root directory path is not set.");
//      Assert.notNull(env.getProperty("egnyte.api-token"), "Egnyte API token is not set.");
//      EgnyteOptions options = new EgnyteOptions();
//      options.setRootUrl(new URL(env.getRequiredProperty("egnyte.root-url")));
//      options.setRootPath(env.getRequiredProperty("egnyte.root-path"));
//      options.setToken(env.getRequiredProperty("egnyte.api-token"));
//      if (env.containsProperty("egnyte.qps")) {
//        options.setQps(env.getRequiredProperty("egnyte.qps", Integer.class));
//      } else if (env.containsProperty("egnyte.sleep")) {
//        options.setSleep(env.getRequiredProperty("egnyte.sleep", Integer.class));
//      }
//      if (env.containsProperty("storage.max-folder-read-depth")) {
//        options.setMaxReadDepth(
//            env.getRequiredProperty("storage.max-folder-read-depth", int.class));
//      }
//      if (env.containsProperty("storage.use-existing")) {
//        options.setUseExisting(env.getRequiredProperty("storage.use-existing", boolean.class));
//      }
//      return options;
//    }

    @Bean
    public EgnyteRestApiClient egnyteClient(Environment env)
        throws Exception {
      return new EgnyteRestApiClient(egnyteRestTemplate(env));
    }

    @Bean
    public EgnyteStudyStorageService egnyteStorageService() {
      return new EgnyteStudyStorageService();
    }

    @Bean
    public EgnyteApiDataFileStorageService egnyteApiDataFileStorageService() {
      return new EgnyteApiDataFileStorageService();
    }
  }

  @Configuration
  @ConditionalOnProperty(name = "aws.region", havingValue = "")
  public static class S3StorageServiceConfiguration {

    @Autowired
    private Environment env;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired(required = false)
    private AwsCredentialsProvider credentialsProvider;

    @Value("${aws.region}")
    private Region region;

    @Bean
    public S3Client s3Client() {
      S3ClientBuilder builder = S3Client.builder().region(region);
      if (credentialsProvider != null) {
        builder.credentialsProvider(credentialsProvider);
      }
      return builder.build();
    }

    @Bean
    public S3DataFileStorageService s3DataFileStorageService() {
      return new S3DataFileStorageService(s3Client());
    }

    @Bean
    @ConditionalOnProperty(name = "aws.s3.default-study-location", havingValue = "")
    public S3StudyFileStorageService s3StudyFileStorageService() {
      return new S3StudyFileStorageService();
    }
  }

}
