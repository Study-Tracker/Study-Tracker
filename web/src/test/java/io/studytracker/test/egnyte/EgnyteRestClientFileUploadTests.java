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

package io.studytracker.test.egnyte;

import io.studytracker.Application;
import io.studytracker.egnyte.entity.EgnyteFile;
import io.studytracker.egnyte.entity.EgnyteObject;
import io.studytracker.egnyte.rest.EgnyteRestApiClient;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"egnyte-test", "example"})
public class EgnyteRestClientFileUploadTests {

  private static final Resource EXAMPLE_FILE = new ClassPathResource("upload-test.txt");

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private EgnyteRestApiClient client;

  @Autowired private Environment env;

  @Test
  public void fileUploadTest() throws Exception {
    String egnyteRoot = env.getRequiredProperty("egnyte.root-path") + "/TestUpload";
    File file = EXAMPLE_FILE.getFile();
    Exception exception = null;
    try {
      client.uploadFile(file, egnyteRoot);
      EgnyteObject egnyteObject = client.findObjectByPath(egnyteRoot + "/upload-test.txt");
      Assert.assertNotNull(egnyteObject);
      Assert.assertFalse(egnyteObject.isFolder());
      EgnyteFile egnyteFile = (EgnyteFile) egnyteObject;
      Assert.assertEquals("upload-test.txt", egnyteFile.getName());
    } catch (Exception ex) {
      exception = ex;
      ex.printStackTrace();
    }
    Assert.assertNull(exception);
  }

  @Test
  public void fileUploadToInvalidFolderTest() throws Exception {
    //    String egnyteRoot = env.getRequiredProperty("egnyte.root-path") + "/TestUpload";
    //    File file = EXAMPLE_FILE.getFile();
    //    Exception exception = null;
    //    try {
    //      egnyteService.uploadFile(file, egnyteRoot + "/bad_folder");
    //    } catch (Exception ex) {
    //      exception = ex;
    //      ex.printStackTrace();
    //    }
    //    Assert.assertNotNull(exception);
  }

  @Test
  public void uploadInvalidFileTest() throws Exception {
    String egnyteRoot = env.getRequiredProperty("egnyte.root-path") + "/TestUpload";
    File file = new File("bad_file.txt");
    Exception exception = null;
    try {
      client.uploadFile(file, egnyteRoot);
    } catch (Exception ex) {
      exception = ex;
      ex.printStackTrace();
    }
    Assert.assertNotNull(exception);
  }
}
