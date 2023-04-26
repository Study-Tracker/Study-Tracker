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

package io.studytracker.gitlab;

import io.studytracker.exception.StudyTrackerException;
import io.studytracker.gitlab.entities.GitLabAuthenticationToken;
import io.studytracker.gitlab.entities.GitLabNamespace;
import io.studytracker.gitlab.entities.GitLabNewGroupRequest;
import io.studytracker.gitlab.entities.GitLabNewProjectRequest;
import io.studytracker.gitlab.entities.GitLabProject;
import io.studytracker.gitlab.entities.GitLabProjectGroup;
import io.studytracker.gitlab.entities.GitLabUser;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public final class GitLabRestClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(GitLabRestClient.class);
  private final RestTemplate restTemplate = new RestTemplate();
  private URL rootUrl;
  private String username;
  private String password;
  private String accessToken;

  public static class GitLabRestClientBuilder {
    private final GitLabRestClient client;

    public GitLabRestClientBuilder() {
      client = new GitLabRestClient();
    }

    public GitLabRestClientBuilder rootUrl(@NotNull String rootUrl) {
      try {
        client.setRootUrl(new URL(rootUrl));
      } catch (MalformedURLException e) {
        throw new StudyTrackerException("Invalid GitLab root URL", e);
      }
      return this;
    }

    public GitLabRestClientBuilder username(@NotNull String username) {
      client.setUsername(username);
      return this;
    }

    public GitLabRestClientBuilder password(@NotNull String password) {
      client.setPassword(password);
      return this;
    }

    public GitLabRestClientBuilder accessToken(@NotNull String accessToken) {
      client.setAccessToken(accessToken);
      return this;
    }

    public GitLabRestClient build() {
      Assert.notNull(client.rootUrl, "GitLab root URL must be set");
      if (client.accessToken == null) {
        Assert.hasText(client.username, "GitLab username must be set");
        Assert.hasText(client.password, "GitLab password must be set");
        client.accessToken = client.authenticate().getAccessToken();
      } else {
        Assert.hasText(client.accessToken, "GitLab access token must be set");
      }
      return client;
    }
  }

  private GitLabRestClient() {
  }
  
  @PostConstruct
  public void init() {
    Assert.notNull(rootUrl, "GitLab root URL must be set");
    if (accessToken == null) {
      Assert.hasText(username, "GitLab username must be set");
      Assert.hasText(password, "GitLab password must be set");
      this.accessToken = authenticate().getAccessToken();
    } else {
      Assert.hasText(accessToken, "GitLab access token must be set");
    }
  }

  /**
   * Authenticates with GitLab server and generates an access token.
   *
   * @return the access token
   */
  public GitLabAuthenticationToken authenticate() {
    URL url = joinUrls(rootUrl, "/oauth/token");
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/x-www-form-urlencoded");
    headers.set("Accept", "application/json");
    MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
    data.add("grant_type", "password");
    data.add("username", username);
    data.add("password", password);
    HttpEntity<?> request = new HttpEntity<>(data, headers);
    ResponseEntity<GitLabAuthenticationToken> response = restTemplate.exchange(
        url.toString(), HttpMethod.POST, request, GitLabAuthenticationToken.class);
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return response.getBody();
    } else {
      throw new StudyTrackerException("Failed to authenticate with GitLab");
    }
  }

  /**
   * Returns a list of users. The list can be filtered using a search string.
   *
   * @param query the search string
   * @return the list of users
   */
  public List<GitLabUser> findUsers(String query) {
    LOGGER.debug("Finding users with query: {}", query);
    URL url = joinUrls(rootUrl,
        "/api/v4/users" + (query != null ? "?search=" + query : ""));
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    headers.set("Accept", "application/json");
    HttpEntity<?> request = new HttpEntity<>(headers);
    ResponseEntity<List<GitLabUser>> response = restTemplate.exchange(
        url.toString(), HttpMethod.GET, request,
        new ParameterizedTypeReference<List<GitLabUser>>() {});
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return response.getBody();
    } else {
      throw new StudyTrackerException("Failed to find users in GitLab");
    }
  }

  /**
   * Returns a list of all registered users.
   *
   * @return the list of users
   */
  public List<GitLabUser> findUsers() {
    return findUsers(null);
  }

  /**
   * Looks up a user by their GitLab ID.
   *
   * @param userId the user ID
   * @return the user or an empty optional if not found
   */
  public Optional<GitLabUser> findUserById(@NotNull Integer userId) {
    LOGGER.debug("Finding user with id: {}", userId);
    URL url = joinUrls(rootUrl,
        "/api/v4/users/" + userId.toString());
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    headers.set("Accept", "application/json");
    HttpEntity<?> request = new HttpEntity<>(headers);
    ResponseEntity<GitLabUser> response = restTemplate.exchange(
        url.toString(), HttpMethod.GET, request, GitLabUser.class);
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return Optional.ofNullable(response.getBody());
    } else {
      return Optional.empty();
    }
  }

  /**
   * Returns a list of group and user namespaces.
   *
   * @param query the search string
   * @return the list of namespaces
   */
  public List<GitLabNamespace> findNamespaces(String query) {
    LOGGER.debug("Finding namespaces with query: {}", query);
    URL url = joinUrls(rootUrl,
        "/api/v4/namespaces" + (query != null ? "?search=" + query : ""));
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    headers.set("Accept", "application/json");
    HttpEntity<?> request = new HttpEntity<>(headers);
    ResponseEntity<List<GitLabNamespace>> response = restTemplate.exchange(
        url.toString(), HttpMethod.GET, request,
        new ParameterizedTypeReference<List<GitLabNamespace>>() {});
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return response.getBody();
    } else {
      throw new StudyTrackerException("Failed to find namespaces in GitLab");
    }
  }

  /**
   * Looks up a namespace by its ID.
   *
   * @param namespaceId the namespace ID
   * @return the namespace or an empty optional if not found
   */
  public Optional<GitLabNamespace> findNamespaceById(@NotNull Integer namespaceId) {
    LOGGER.debug("Finding namespace with id: {}", namespaceId);
    URL url = joinUrls(rootUrl,
        "/api/v4/namespaces/" + namespaceId.toString());
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    headers.set("Accept", "application/json");
    HttpEntity<?> request = new HttpEntity<>(headers);
    ResponseEntity<GitLabNamespace> response = restTemplate.exchange(
        url.toString(), HttpMethod.GET, request, GitLabNamespace.class);
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return Optional.ofNullable(response.getBody());
    } else {
      return Optional.empty();
    }
  }

  /**
   * Returns a list of all group and user namespaces.
   *
   * @return the list of namespaces
   */
  public List<GitLabNamespace> findNamespaces() {
    return findNamespaces(null);
  }

  /**
   * Returns a list of public groups. The list can be filtered using a search string.
   *
   * @param query the search string
   * @return the list of groups
   */
  public List<GitLabProjectGroup> findGroups(String query) {
    LOGGER.debug("Finding groups with query: {}", query);
    URL url = joinUrls(rootUrl,
        "/api/v4/groups" + (query != null ? "?search=" + query : ""));
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    headers.set("Accept", "application/json");
    HttpEntity<?> request = new HttpEntity<>(headers);
    ResponseEntity<List<GitLabProjectGroup>> response = restTemplate.exchange(
        url.toString(), HttpMethod.GET, request,
        new ParameterizedTypeReference<List<GitLabProjectGroup>>() {});
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return response.getBody();
    } else {
      throw new StudyTrackerException("Failed to find groups in GitLab");
    }
  }

  /**
   * Returns a list of all public groups.
   *
   * @return the list of groups
   */
  public List<GitLabProjectGroup> findGroups() {
    return findGroups(null);
  }

  /**
   * Looks up a group by its ID.
   *
   * @param groupId the group ID
   * @return the group or an empty optional if not found
   */
  public Optional<GitLabProjectGroup> findGroupById(@NotNull Integer groupId) {
    LOGGER.debug("Finding group with id: {}", groupId);
    URL url = joinUrls(rootUrl,
        "/api/v4/groups/" + groupId.toString());
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    headers.set("Accept", "application/json");
    HttpEntity<?> request = new HttpEntity<>(headers);
    ResponseEntity<GitLabProjectGroup> response = restTemplate.exchange(
        url.toString(), HttpMethod.GET, request, GitLabProjectGroup.class);
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return Optional.ofNullable(response.getBody());
    } else {
      return Optional.empty();
    }
  }

  public GitLabProjectGroup createNewGroup(@NotNull GitLabNewGroupRequest newGroupRequest) {
    LOGGER.debug("Creating new group with name: {}", newGroupRequest.getName());
    URL url = joinUrls(rootUrl, "/api/v4/groups");
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    HttpEntity<GitLabNewGroupRequest> request = new HttpEntity<>(newGroupRequest, headers);
    ResponseEntity<GitLabProjectGroup> response = restTemplate.exchange(
        url.toString(), HttpMethod.POST, request, GitLabProjectGroup.class);
    if (response.getStatusCode().equals(HttpStatus.CREATED)) {
      return response.getBody();
    } else {
      throw new StudyTrackerException("Failed to create new group in GitLab");
    }
  }

  /**
   * Returns a filtered list of projects (aka. Git repositories).
   *
   * @param query the search string
   * @return the list of projects
   */
  public List<GitLabProject> findProjects(String query) {
    LOGGER.debug("Finding projects with query: {}", query);
    URL url = joinUrls(rootUrl,
        "/api/v4/projects" + (query != null ? "?search=" + query : ""));
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    headers.set("Accept", "application/json");
    HttpEntity<?> request = new HttpEntity<>(headers);
    ResponseEntity<List<GitLabProject>> response = restTemplate.exchange(
        url.toString(), HttpMethod.GET, request,
        new ParameterizedTypeReference<List<GitLabProject>>() {});
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return response.getBody();
    } else {
      throw new StudyTrackerException("Failed to find projects in GitLab");
    }
  }

  /**
   * Returns a list of all projects (aka. Git repositories).
   * @return the list of projects
   */
  public List<GitLabProject> findProjects() {
    return findProjects(null);
  }

  /**
   * Returns reference to a project, identified by its ID.
   *
   * @param projectId the project ID
   * @return the project or an empty optional if not found
   */
  public Optional<GitLabProject> findProjectById(@NotNull Integer projectId) {
    LOGGER.debug("Finding project with id: {}", projectId);
    URL url = joinUrls(rootUrl,
        "/api/v4/projects/" + projectId.toString());
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    headers.set("Accept", "application/json");
    HttpEntity<?> request = new HttpEntity<>(headers);
    ResponseEntity<GitLabProject> response = restTemplate.exchange(
        url.toString(), HttpMethod.GET, request, GitLabProject.class);
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return Optional.ofNullable(response.getBody());
    } else {
      return Optional.empty();
    }
  }

  /**
   * Creates a new GitLab project (aka. Git repository) for the given user.
   *
   * @param user the user to assign as owner, if present
   * @param newProjectRequest the project creation request
   * @return
   */
  public GitLabProject createProject(
      @NotNull GitLabNewProjectRequest newProjectRequest,
      GitLabUser user
  ) {
    LOGGER.info("Creating project: {}", newProjectRequest);
    URL url = joinUrls(rootUrl, "/api/v4/projects"
        + (user != null ? "/user/" + user.getId().toString() : ""));
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    HttpEntity<?> request = new HttpEntity<>(newProjectRequest, headers);
    ResponseEntity<GitLabProject> response = restTemplate.exchange(
        url.toString(), HttpMethod.POST, request, GitLabProject.class);
    if (response.getStatusCode().equals(HttpStatus.CREATED)) {
      return response.getBody();
    } else {
      throw new StudyTrackerException("Failed to create project in GitLab");
    }
  }

  /**
   * Creates a new project and assigns it to the user whose credentials were used to authenticate.
   *
   * @param newProjectRequest the project creation request
   * @return
   */
  public GitLabProject createProject(@NotNull GitLabNewProjectRequest newProjectRequest) {
    return createProject(newProjectRequest, null);
  }

  private URL joinUrls(URL root, String path) {
    try {
      return new URL(root, path);
    } catch (MalformedURLException ex) {
      throw new StudyTrackerException(ex);
    }
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public void setRootUrl(URL rootUrl) {
    this.rootUrl = rootUrl;
  }
}
