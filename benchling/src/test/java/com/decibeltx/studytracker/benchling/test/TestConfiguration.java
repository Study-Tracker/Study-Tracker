package com.decibeltx.studytracker.benchling.test;

import com.decibeltx.studytracker.benchling.eln.BenchlingElnServiceConfiguration;
import com.decibeltx.studytracker.core.config.ExampleDataConfiguration;
import com.decibeltx.studytracker.core.config.LocalEventsConfiguration;
import com.decibeltx.studytracker.core.config.LocalStudyStorageServiceConfiguration;
import com.decibeltx.studytracker.core.config.MongoDataSourceConfiguration;
import com.decibeltx.studytracker.core.config.MongoRepositoryConfiguration;
import com.decibeltx.studytracker.core.config.ServiceConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    MongoRepositoryConfiguration.class,
    ServiceConfiguration.class,
    MongoDataSourceConfiguration.class,
    ExampleDataConfiguration.class,
    LocalStudyStorageServiceConfiguration.class,
    LocalEventsConfiguration.class,
    BenchlingElnServiceConfiguration.class
})
public class TestConfiguration {

}
