package com.decibeltx.studytracker.aws.test;

import com.decibeltx.studytracker.aws.AwsServiceConfiguration;
import com.decibeltx.studytracker.aws.EventBridgeConfiguration;
import com.decibeltx.studytracker.core.config.ExampleDataConfiguration;
import com.decibeltx.studytracker.core.config.LocalStudyStorageServiceConfiguration;
import com.decibeltx.studytracker.core.config.MongoDataSourceConfiguration;
import com.decibeltx.studytracker.core.config.MongoRepositoryConfiguration;
import com.decibeltx.studytracker.core.config.ServiceConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Import({
    MongoRepositoryConfiguration.class,
    ServiceConfiguration.class,
    MongoDataSourceConfiguration.class,
    ExampleDataConfiguration.class,
    LocalStudyStorageServiceConfiguration.class,
    AwsServiceConfiguration.class,
    EventBridgeConfiguration.class
})
@PropertySource("classpath:test.properties")
public class TestConfiguration {

}
