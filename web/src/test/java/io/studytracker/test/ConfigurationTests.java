package io.studytracker.test;

import io.studytracker.Application;
import io.studytracker.config.HostInformation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
public class ConfigurationTests {

  @Autowired
  private ApplicationContext context;

  @Test
  public void contextLoads() {
    Assert.assertNotNull(context);
  }

  @Test
  public void hostInformationTest() {
    HostInformation hostInformation = context.getBean(HostInformation.class);
    Assert.assertNotNull(hostInformation);
    System.out.println(hostInformation.toString());
    Assert.assertEquals("localhost", hostInformation.getHostName());
    Assert.assertFalse(hostInformation.getJavaVersion().startsWith("@"));
    Assert.assertFalse(hostInformation.getApplicationVersion().startsWith("@"));
    Assert.assertEquals("http://localhost:0", hostInformation.getApplicationUrl());
  }

}
