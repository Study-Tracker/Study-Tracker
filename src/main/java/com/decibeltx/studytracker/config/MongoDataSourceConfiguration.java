/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.decibeltx.studytracker.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.util.Assert;

@Configuration
@ConditionalOnMissingBean(MongoClient.class)
public class MongoDataSourceConfiguration extends AbstractMongoClientConfiguration {

  @Autowired
  private Environment env;

  @Override
  public MongoClient mongoClient() {
    Assert.notNull(env.getProperty("db.username"), "Data source username must not be null!");
    Assert.notNull(env.getProperty("db.name"), "Database name must not be null!");
    Assert.notNull(env.getProperty("db.password"), "Data source password must not be null!");
    MongoClientSettings settings = MongoClientSettings
        .builder()
        .credential(MongoCredential.createCredential(
            env.getRequiredProperty("db.username"),
            env.getRequiredProperty("db.name"),
            env.getRequiredProperty("db.password").toCharArray()
        ))
        .applyConnectionString(new ConnectionString(env.getRequiredProperty("db.connectionString")))
        .build();
    return MongoClients.create(settings);
  }

  @Override
  protected String getDatabaseName() {
    return env.getRequiredProperty("db.name");
  }

}
