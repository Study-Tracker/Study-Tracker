package com.decibeltx.studytracker.config;

import javax.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.decibeltx.studytracker.repository")
@EnableTransactionManagement
@EnableJpaAuditing
public class JpaRepositoryConfiguration {

//  @Bean
//  public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
//
//    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//    vendorAdapter.setGenerateDdl(true);
//
//    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
//    factory.setJpaVendorAdapter(vendorAdapter);
//    factory.setPackagesToScan("com.decibeltx.studytracker.model");
//    factory.setDataSource(dataSource);
//    return factory;
//
//  }

  @Bean
  public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    JpaTransactionManager txManager = new JpaTransactionManager();
    txManager.setEntityManagerFactory(entityManagerFactory);
    return txManager;
  }

}
