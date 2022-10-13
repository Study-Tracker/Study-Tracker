package io.studytracker.test.gitlab;

import io.studytracker.Application;
import io.studytracker.gitlab.GitLabOptions;
import io.studytracker.gitlab.GitLabRestClient;
import io.studytracker.gitlab.entities.GitLabAuthenticationToken;
import io.studytracker.gitlab.entities.GitLabUser;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"gitlab-test", "example"})
public class GitLabRestClientTests {

  @Autowired private Environment env;

  private GitLabRestClient client;

  @PostConstruct
  public void init() throws Exception {
    GitLabOptions options = new GitLabOptions();
    options.setRootUrl(new URL(env.getRequiredProperty("gitlab.url")));
    options.setClientId(env.getRequiredProperty("gitlab.application-id"));
    options.setClientSecret(env.getRequiredProperty("gitlab.secret"));
    options.setUsername(env.getRequiredProperty("gitlab.username"));
    options.setPassword(env.getRequiredProperty("gitlab.password"));
    client = new GitLabRestClient(new RestTemplate(), options);
  }

  @Test
  public void authenticationTest() throws Exception {
    GitLabAuthenticationToken token = client.authenticate();
    System.out.println(token.toString());
    Assert.assertNotNull(token);
    Assert.assertTrue(token.getAccessToken().length() > 0);
  }

  @Test
  public void findUsersTest() throws Exception {

    GitLabAuthenticationToken token = client.authenticate();

    List<GitLabUser> users = client.findUsers(token.getAccessToken());
    Assert.assertNotNull(users);
    Assert.assertFalse(users.isEmpty());
    Assert.assertTrue(users.size() > 1);

    users = client.findUsers(token.getAccessToken(), "oemler");
    Assert.assertNotNull(users);
    Assert.assertFalse(users.isEmpty());
    Assert.assertEquals(users.size(), 1);

    GitLabUser user = users.get(0);
    System.out.println(user.toString());

    Optional<GitLabUser> optional = client.findUserById(token.getAccessToken(), user.getId());
    Assert.assertTrue(optional.isPresent());
    Assert.assertEquals(optional.get().getName(), user.getName());

  }

}
