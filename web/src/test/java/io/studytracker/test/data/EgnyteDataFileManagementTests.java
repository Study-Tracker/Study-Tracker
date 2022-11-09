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

package io.studytracker.test.data;

import io.studytracker.Application;
import io.studytracker.egnyte.EgnyteApiDataFileStorageService;
import io.studytracker.egnyte.entity.EgnyteFolder;
import io.studytracker.egnyte.entity.EgnyteObject;
import io.studytracker.egnyte.integration.EgnyteIntegrationOptions;
import io.studytracker.egnyte.integration.EgnyteIntegrationOptionsFactory;
import io.studytracker.egnyte.rest.EgnyteRestApiClient;
import io.studytracker.integration.IntegrationType;
import io.studytracker.model.FileStorageLocation;
import io.studytracker.model.IntegrationInstance;
import io.studytracker.repository.IntegrationInstanceRepository;
import io.studytracker.service.StorageLocationService;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import java.net.URL;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"egnyte-test"})
public class EgnyteDataFileManagementTests {

  @Autowired
  private Environment env;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private EgnyteRestApiClient client;

  @Autowired
  private EgnyteApiDataFileStorageService storageService;

  @Autowired
  private StorageLocationService storageLocationService;

  @Autowired
  private IntegrationInstanceRepository integrationInstanceRepository;

  private EgnyteIntegrationOptions options;

  @Before
  private void getOptionsFromLocation() {
    IntegrationInstance instance = integrationInstanceRepository
        .findByIntegrationType(IntegrationType.EGNYTE)
        .get(0);
    options = EgnyteIntegrationOptionsFactory.create(instance);
  }

  @Test
  public void getRootFolderTest() throws Exception {
    String rootPath = env.getRequiredProperty("egnyte.root-path");
    String token = env.getRequiredProperty("egnyte.api-token");
    URL url = new URL(env.getRequiredProperty("egnyte.root-url"));
    EgnyteObject egnyteObject = client.findObjectByPath(url, rootPath, token);
    Assert.assertNotNull(egnyteObject);
    Assert.assertTrue(egnyteObject.isFolder());
    EgnyteFolder folder = (EgnyteFolder) egnyteObject;
    System.out.println(folder.toString());
  }

  @Test
  public void getFolderContentsTest() throws Exception {
    String rootPath = env.getRequiredProperty("egnyte.root-path");
    FileStorageLocation location = new FileStorageLocation();
    location.setRootFolderPath(rootPath);
    StorageFolder folder = storageService.findFolderByPath(location, rootPath);
    Assert.assertNotNull(folder);
    for (StorageFolder subFolder : folder.getSubFolders()) {
      System.out.println(subFolder.toString());
    }
    for (StorageFile file : folder.getFiles()) {
      System.out.println(file.toString());
    }
  }

}
