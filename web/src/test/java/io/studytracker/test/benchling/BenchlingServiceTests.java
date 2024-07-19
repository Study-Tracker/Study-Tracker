/*
 * Copyright 2019-2024 the original author or authors.
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

package io.studytracker.test.benchling;

import io.studytracker.Application;
import io.studytracker.benchling.BenchlingNotebookFolderService;
import io.studytracker.benchling.BenchlingNotebookUserService;
import io.studytracker.eln.NotebookFolder;
import io.studytracker.eln.NotebookUser;
import io.studytracker.model.ELNFolder;
import io.studytracker.model.User;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
@ActiveProfiles({"benchling-test", "example"})
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class BenchlingServiceTests {

  @Autowired
  private BenchlingNotebookFolderService benchlingNotebookFolderService;

  @Autowired
  private BenchlingNotebookUserService benchlingNotebookUserService;

  @Test
  public void findProjectsFoldersTest() {
    List<ELNFolder> folders = benchlingNotebookFolderService.listProjectFolders();
    Assert.assertNotNull(folders);
    Assert.assertFalse(folders.isEmpty());
    for (NotebookFolder folder: folders) {
      System.out.println(folder);
    }
  }

  @Test
  public void findUserTest() {
    User user = new User();
    user.setUsername("woemler@vesaliustx.com");
    user.setEmail("woemler@vesaliustx.com");
    user.setDisplayName("Will Oemler");
    Optional<NotebookUser> optional = benchlingNotebookUserService.findNotebookUser(user);
    Assert.assertTrue(optional.isPresent());
    NotebookUser notebookUser = optional.get();
    Assert.assertEquals(notebookUser.getEmail(), user.getEmail());
  }

  @Test
  public void getFolderIdFromUrlTest() {
    String url = "https://testorg.benchling.com/testorg/f/lib_iQIYV5t4-ti-123-lorem-ipsum/prt_SgZ7O6Vt-lorem-ipsum/edit";
    String regex = "lib_[a-zA-Z0-9]+";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(url);
    Assert.assertTrue(matcher.find());
    Assert.assertEquals("lib_iQIYV5t4", matcher.group(0));
  }

}
