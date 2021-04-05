package com.decibeltx.studytracker.test.benchling;

import com.decibeltx.studytracker.Application;
import com.decibeltx.studytracker.benchling.eln.BenchlingElnRestClient;
import com.decibeltx.studytracker.benchling.eln.entities.BenchlingFolder;
import com.decibeltx.studytracker.benchling.eln.entities.BenchlingProject;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
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
@ActiveProfiles({"benchling-test", "example"})
public class BenchlingElnRestClientTests {

  @Autowired
  private BenchlingElnRestClient client;

  @Autowired
  private Environment env;

  @Test
  public void findProjectsTest() {
    List<BenchlingProject> projects = client.findProjects();
    Assert.assertNotNull(projects);
    Assert.assertFalse(projects.isEmpty());
    for (BenchlingProject project : projects) {
      System.out.println(project.toString());
    }
  }

  @Test
  public void findProjectByIdTest() throws Exception {
    BenchlingProject project = client.findProjects().stream().findFirst().get();
    Optional<BenchlingProject> projectOptional = client.findProjectById(project.getId());
    Assert.assertTrue(projectOptional.isPresent());
    BenchlingProject project2 = projectOptional.get();
    Assert.assertEquals(project.getName(), project2.getName());
  }

  @Test
  public void findRootFoldersTest() throws Exception {
    List<BenchlingFolder> folders = client.findRootFolders();
    Assert.assertFalse(folders.isEmpty());
    for (BenchlingFolder folder : folders) {
      System.out.println(folder.toString());
    }
  }

  @Test
  public void findProjectFolderChildrenTest() throws Exception {
    BenchlingProject project = client.findProjects().stream()
        .filter(p -> p.getName().equals(env.getRequiredProperty("notebook.test.default-project")))
        .findFirst()
        .get();
    List<BenchlingFolder> folders = client.findProjectFolderChildren(project.getId());
    Assert.assertFalse(folders.isEmpty());
    for (BenchlingFolder folder : folders) {
      System.out.println(folder.toString());
    }
  }

  @Test
  public void findFolderChildrenTest() throws Exception {
    BenchlingProject project = client.findProjects().stream()
        .filter(p -> p.getName().equals(env.getRequiredProperty("notebook.test.default-project")))
        .findFirst()
        .get();
    BenchlingFolder parentFolder = client.findRootFolders().stream()
        .filter(f -> f.getProjectId().equals(project.getId()))
        .findFirst()
        .get();
    List<BenchlingFolder> folders = client.findFolderChildren(parentFolder.getId());
    Assert.assertFalse(folders.isEmpty());
    for (BenchlingFolder folder : folders) {
      System.out.println(folder.toString());
    }
  }

  @Test
  public void findFolderByIdTest() throws Exception {
    BenchlingProject project = client.findProjects().stream()
        .filter(p -> p.getName().equals(env.getRequiredProperty("notebook.test.default-project")))
        .findFirst()
        .get();
    BenchlingFolder parentFolder = client.findRootFolders().stream()
        .filter(f -> f.getProjectId().equals(project.getId()))
        .findFirst()
        .get();
    Optional<BenchlingFolder> folderOptional = client.findFolderById(parentFolder.getId());
    Assert.assertTrue(folderOptional.isPresent());
    BenchlingFolder folder = folderOptional.get();
    Assert.assertEquals(parentFolder.getName(), folder.getName());
  }

}
