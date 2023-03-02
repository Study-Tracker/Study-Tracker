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

package io.studytracker.test.data;

import io.studytracker.Application;
import io.studytracker.egnyte.EgnyteStudyStorageService;
import io.studytracker.egnyte.entity.EgnyteFolder;
import io.studytracker.egnyte.entity.EgnyteObject;
import io.studytracker.egnyte.rest.EgnyteRestApiClient;
import io.studytracker.model.EgnyteDrive;
import io.studytracker.model.EgnyteIntegration;
import io.studytracker.repository.EgnyteDriveRepository;
import io.studytracker.repository.EgnyteIntegrationRepository;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"egnyte-test"})
public class EgnyteDataFileManagementTests {

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private EgnyteRestApiClient client;

  @Autowired
  private EgnyteStudyStorageService storageService;

  @Autowired
  private EgnyteIntegrationRepository egnyteIntegrationRepository;

  @Autowired
  private EgnyteDriveRepository egnyteDriveRepository;

  @Test
  public void getRootFolderTest() throws Exception {
    EgnyteIntegration integration = egnyteIntegrationRepository.findAll().get(0);
    EgnyteDrive drive = egnyteDriveRepository.findByIntegrationId(integration.getId()).get(0);
    String rootPath = drive.getStorageDrive().getRootPath();
    String token = integration.getApiToken();
    URL url = new URL(integration.getRootUrl());
    EgnyteObject egnyteObject = client.findObjectByPath(url, rootPath, token);
    Assert.assertNotNull(egnyteObject);
    Assert.assertTrue(egnyteObject.isFolder());
    EgnyteFolder folder = (EgnyteFolder) egnyteObject;
    System.out.println(folder.toString());
  }

  @Test
  public void getFolderContentsTest() throws Exception {
    EgnyteIntegration integration = egnyteIntegrationRepository.findAll().get(0);
    EgnyteDrive drive = egnyteDriveRepository.findByIntegrationId(integration.getId()).get(0);
    String rootPath = drive.getStorageDrive().getRootPath();
    StorageFolder folder = storageService.findFolderByPath(drive.getStorageDrive(), rootPath);
    Assert.assertNotNull(folder);
    for (StorageFolder subFolder : folder.getSubFolders()) {
      System.out.println(subFolder.toString());
    }
    for (StorageFile file : folder.getFiles()) {
      System.out.println(file.toString());
    }
  }

}
