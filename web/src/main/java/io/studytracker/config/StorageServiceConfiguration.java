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
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.studytracker.aws.S3DataFileStorageService;
import io.studytracker.aws.S3StudyFileStorageService;
import io.studytracker.config.properties.EgnyteProperties;
import io.studytracker.egnyte.EgnyteApiDataFileStorageService;
import io.studytracker.egnyte.EgnyteFolderNamingService;
import io.studytracker.egnyte.EgnyteStudyStorageService;
import io.studytracker.egnyte.entity.EgnyteObject;
import io.studytracker.egnyte.rest.EgnyteObjectDeserializer;
import io.studytracker.egnyte.rest.EgnyteRestApiClient;
import io.studytracker.exception.InvalidConfigurationException;
import io.studytracker.storage.LocalFileSystemStorageService;
import java.net.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StringUtils;
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
    public LocalFileSystemStorageService localFileSystemStudyStorageService() {
      return new LocalFileSystemStorageService();
    }
  }

  @Configuration
  @ConditionalOnProperty(name = "storage.mode", havingValue = "egnyte")
  @AutoConfigureBefore(StorageServiceConfiguration.class)
  public static class EgnyteServiceConfiguration {

    @Bean
    public EgnyteFolderNamingService egnyteFolderNamingService() {
      return new EgnyteFolderNamingService();
    }

    @Bean
    public ObjectMapper egnyteObjectMapper(EgnyteProperties egnyteProperties) throws Exception {
      String url;
      if (StringUtils.hasText(egnyteProperties.getRootUrl())) {
        url = egnyteProperties.getRootUrl();
      } else if (StringUtils.hasText(egnyteProperties.getTenantName())) {
        url = "https://" + egnyteProperties.getTenantName() + ".egnyte.com";
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
    public RestTemplate egnyteRestTemplate(EgnyteProperties egnyteProperties) throws Exception {
      RestTemplate restTemplate = new RestTemplateBuilder().build();
      MappingJackson2HttpMessageConverter httpMessageConverter =
          new MappingJackson2HttpMessageConverter();
      httpMessageConverter.setObjectMapper(egnyteObjectMapper(egnyteProperties));
      restTemplate.getMessageConverters().add(0, httpMessageConverter);
      return restTemplate;
    }

    @Bean
    public EgnyteRestApiClient egnyteClient(EgnyteProperties egnyteProperties)
        throws Exception {
      return new EgnyteRestApiClient(egnyteRestTemplate(egnyteProperties));
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
