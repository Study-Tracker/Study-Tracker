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

package io.studytracker.test.egnyte;

import io.studytracker.Application;
import io.studytracker.egnyte.entity.EgnyteFile;
import io.studytracker.egnyte.entity.EgnyteFolder;
import io.studytracker.egnyte.entity.EgnyteObject;
import io.studytracker.egnyte.exception.ObjectNotFoundException;
import io.studytracker.egnyte.rest.EgnyteRestApiClient;
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"egnyte-test", "example"})
public class EgnyteRestClientFolderBrowsingTests {

  private static final String EGNYTE_ROOT =
      "Shared/StudyTrackerDemo/Test";

  private static final String FOLDER_ID = "6e56926d-095e-4e38-9cb1-138157682cff";

  private static final String FILE_ID = "24d19373-f172-45f3-a8d6-92e4f2996ac3";

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private EgnyteRestApiClient client;

  @Value("${egnyte.root-url}")
  private String rootUrl;

  @Value("${egnyte.api-token}")
  private String token;

  @Test
  public void listFolderContentsTest() throws Exception {
    EgnyteObject egnyteObject = client.findObjectByPath(new URL(rootUrl), EGNYTE_ROOT, token);
    Assert.assertTrue(egnyteObject.isFolder());
    EgnyteFolder folder = (EgnyteFolder) egnyteObject;
    System.out.println(folder);
    Assert.assertNotNull(folder);
    Assert.assertTrue(folder.isFolder());
    Assert.assertEquals(folder.getName(), "Test");
    Assert.assertNotNull(folder.getUrl());
    Assert.assertFalse(folder.getSubFolders().isEmpty());
    Assert.assertEquals(folder.getSubFolders().size(), 1);
    EgnyteFolder subfolder = folder.getSubFolders().get(0);
    Assert.assertEquals(subfolder.getName(), "Subfolder");
    Assert.assertEquals(folder.getFiles().size(), 1);
    EgnyteFile egnyteFile = folder.getFiles().get(0);
    Assert.assertEquals(egnyteFile.getName(), "test.txt");
    Assert.assertNotNull(egnyteFile.getUrl());
  }

  @Test
  public void invalidFolderTest() throws Exception {
    Exception exception = null;
    EgnyteObject folder = null;
    try {
      folder = client.findObjectByPath(new URL(rootUrl), "bad/folder", token);
    } catch (Exception e) {
      exception = e;
      e.printStackTrace();
    }
    Assert.assertNotNull(exception);
    Assert.assertNull(folder);
    Assert.assertTrue(exception instanceof ObjectNotFoundException);
  }

  @Test
  public void subfolderNavigationTest() throws Exception {
    URL url = new URL(rootUrl);
    EgnyteObject egnyteObject = client.findObjectByPath(url, EGNYTE_ROOT, token);
    Assert.assertTrue(egnyteObject.isFolder());
    EgnyteFolder folder = (EgnyteFolder) egnyteObject;
    Assert.assertNotNull(folder);
    Assert.assertTrue(folder.isFolder());
    Assert.assertFalse(folder.getSubFolders().isEmpty());
    EgnyteObject subfolderObject = client.findObjectByPath(url, folder.getSubFolders().get(0).getPath(), token);
    Assert.assertTrue(subfolderObject.isFolder());
    EgnyteFolder subfolder = (EgnyteFolder) subfolderObject;
    Assert.assertNotNull(subfolder);
    Assert.assertTrue(subfolder.isFolder());
    Assert.assertTrue(subfolder.getSubFolders().isEmpty());
    Assert.assertFalse(subfolder.getFiles().isEmpty());
    Assert.assertEquals(subfolder.getFiles().get(0).getName(), "another_test.txt");
  }

  @Test
  public void findFolderByIdTest() throws Exception {
    EgnyteFolder folder = client.findFolderById(new URL(rootUrl), FOLDER_ID, token);
    Assert.assertNotNull(folder);
    Assert.assertTrue(folder.isFolder());
    Assert.assertEquals(folder.getName(), "Test");
    Assert.assertFalse(folder.getSubFolders().isEmpty());
    Assert.assertEquals(folder.getSubFolders().size(), 1);
    EgnyteFolder subfolder = folder.getSubFolders().get(0);
    Assert.assertEquals(subfolder.getName(), "Subfolder");
    Assert.assertEquals(folder.getFiles().size(), 1);
    EgnyteFile egnyteFile = folder.getFiles().get(0);
    Assert.assertEquals(egnyteFile.getName(), "test.txt");
    System.out.println(folder);
  }

  @Test
  public void findFolderByInvalidIdTest() throws Exception {
    Exception exception = null;
    EgnyteFolder folder = null;
    try {
      folder = client.findFolderById(new URL(rootUrl), "bad-id", token);
    } catch (Exception e) {
      exception = e;
      e.printStackTrace();
    }
    Assert.assertNotNull(exception);
    Assert.assertNull(folder);
    Assert.assertTrue(exception instanceof ObjectNotFoundException);
  }

  @Test
  public void findFileByPathTest() throws Exception {
    EgnyteObject egnyteObject = client.findObjectByPath(new URL(rootUrl), EGNYTE_ROOT + "/test.txt", token);
    Assert.assertNotNull(egnyteObject);
    Assert.assertFalse(egnyteObject.isFolder());
    EgnyteFile file = (EgnyteFile) egnyteObject;
    Assert.assertEquals("test.txt", file.getName());
  }

  @Test
  public void findFileByInvalidPathTest() throws Exception {
    Exception exception = null;
    EgnyteObject egnyteObject = null;
    try {
      egnyteObject = client.findObjectByPath(new URL(rootUrl), EGNYTE_ROOT + "/bad-file.txt", token);
    } catch (Exception e) {
      exception = e;
      e.printStackTrace();
    }
    Assert.assertNotNull(exception);
    Assert.assertNull(egnyteObject);
    Assert.assertTrue(exception instanceof ObjectNotFoundException);
  }

  @Test
  public void findFileByIdTest() throws Exception {
    EgnyteFile file = client.findFileById(new URL(rootUrl), FILE_ID, token);
    Assert.assertNotNull(file);
    Assert.assertEquals("test.txt", file.getName());
  }

  @Test
  public void findFileByInvalidIdTest() throws Exception {
    Exception exception = null;
    EgnyteFile file = null;
    try {
      file = client.findFileById(new URL(rootUrl), "bad-id", token);
    } catch (Exception e) {
      exception = e;
      e.printStackTrace();
    }
    Assert.assertNotNull(exception);
    Assert.assertNull(file);
    Assert.assertTrue(exception instanceof ObjectNotFoundException);
  }
}
