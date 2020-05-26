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

package com.decibeltx.studytracker.egnyte.test;

import com.decibeltx.studytracker.egnyte.entity.EgnyteFile;
import com.decibeltx.studytracker.egnyte.entity.EgnyteFolder;
import com.decibeltx.studytracker.egnyte.entity.EgnyteObject;
import com.decibeltx.studytracker.egnyte.exception.ObjectNotFoundException;
import com.decibeltx.studytracker.egnyte.rest.EgnyteRestApiClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles({"example"})
public class EgnyteRestClientFolderBrowsingTests {

  private final static String EGNYTE_ROOT = "Shared/General/Informatics & IT/Egnyte API Testing/StudyTrackerTest/Test";

  private final static String FOLDER_ID = "efd92932-46c1-4f50-877f-a31128d3b71e";

  private static final String FILE_ID = "158be718-e35c-4abd-a5a4-b96b61bfb2fe";

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private EgnyteRestApiClient client;

  @Test
  public void listFolderContentsTest() throws Exception {
    EgnyteObject egnyteObject = client.findObjectByPath(EGNYTE_ROOT);
    Assert.assertTrue(egnyteObject.isFolder());
    EgnyteFolder folder = (EgnyteFolder) egnyteObject;
    System.out.println(folder.toString());
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
      folder = client.findObjectByPath("bad/folder");
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
    EgnyteObject egnyteObject = client.findObjectByPath(EGNYTE_ROOT);
    Assert.assertTrue(egnyteObject.isFolder());
    EgnyteFolder folder = (EgnyteFolder) egnyteObject;
    Assert.assertNotNull(folder);
    Assert.assertTrue(folder.isFolder());
    Assert.assertFalse(folder.getSubFolders().isEmpty());
    EgnyteObject subfolderObject = client
        .findObjectByPath(folder.getSubFolders().get(0).getPath());
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
    EgnyteFolder folder = client.findFolderById(FOLDER_ID);
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
    System.out.println(folder.toString());
  }

  @Test
  public void findFolderByInvalidIdTest() throws Exception {
    Exception exception = null;
    EgnyteFolder folder = null;
    try {
      folder = client.findFolderById("bad-id");
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
    EgnyteObject egnyteObject = client.findObjectByPath(EGNYTE_ROOT + "/test.txt");
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
      egnyteObject = client.findObjectByPath(EGNYTE_ROOT + "/bad-file.txt");
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
    EgnyteFile file = client.findFileById(FILE_ID);
    Assert.assertNotNull(file);
    Assert.assertEquals("test.txt", file.getName());
  }

  @Test
  public void findFileByInvalidIdTest() throws Exception {
    Exception exception = null;
    EgnyteFile file = null;
    try {
      file = client.findFileById("bad-id");
    } catch (Exception e) {
      exception = e;
      e.printStackTrace();
    }
    Assert.assertNotNull(exception);
    Assert.assertNull(file);
    Assert.assertTrue(exception instanceof ObjectNotFoundException);
  }

}
