/*
 * Copyright 2019-2025 the original author or authors.
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

package io.studytracker.test.export;

import io.studytracker.Application;
import io.studytracker.example.ExampleDataRunner;
import io.studytracker.export.DataExportService;
import java.nio.file.Path;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "example"})
public class DataExportTests {

  @Autowired private DataExportService dataExportService;
  @Autowired private ExampleDataRunner exampleDataRunner;

  @Before
  public void setup() {
    exampleDataRunner.clearDatabase();
    exampleDataRunner.populateDatabase();
  }

  @Test
  public void dataExportTest() {
    Exception exception = null;
    Path path = null;
    try {
      path = dataExportService.exportAllDataToCsv();
    } catch (Exception e) {
      e.printStackTrace();
      exception = e;
    }
    Assert.assertNull(exception);
    Assert.assertNotNull(path);
    System.out.println(path.toString());
  }

}
