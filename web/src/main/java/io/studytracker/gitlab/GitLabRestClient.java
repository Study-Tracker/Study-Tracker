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

package io.studytracker.gitlab;

import io.studytracker.exception.StudyTrackerException;
import io.studytracker.gitlab.entities.GitLabAuthenticationToken;
import io.studytracker.gitlab.entities.GitLabGroup;
import io.studytracker.gitlab.entities.GitLabNamespace;
import io.studytracker.gitlab.entities.GitLabNewGroupRequest;
import io.studytracker.gitlab.entities.GitLabNewProjectRequest;
import io.studytracker.gitlab.entities.GitLabProject;
import io.studytracker.gitlab.entities.GitLabUser;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public final class GitLabRestClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(GitLabRestClient.class);

  private final RestTemplate restTemplate;
  private final GitLabOptions options;


  public GitLabRestClient(RestTemplate restTemplate, GitLabOptions options) {
    this.restTemplate = restTemplate;
    this.options = options;
  }

  /**
   * Authenticates with GitLab server and generates an access token.
   *
   * @return the access token
   */
  public GitLabAuthenticationToken authenticate() {
    URL url = joinUrls(options.getRootUrl(), "/oauth/token");
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/x-www-form-urlencoded");
    headers.set("Accept", "application/json");
    MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
    data.add("grant_type", "password");
    data.add("username", options.getUsername());
    data.add("password", options.getPassword());
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
   * @param token the access token
   * @param query the search string
   * @return the list of users
   */
  public List<GitLabUser> findUsers(@NotNull String token, String query) {
    LOGGER.debug("Finding users with query: {}", query);
    URL url = joinUrls(options.getRootUrl(),
        "/api/v4/users" + (query != null ? "?search=" + query : ""));
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);
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
   * @param token the access token
   * @return the list of users
   */
  public List<GitLabUser> findUsers(@NotNull String token) {
    return findUsers(token, null);
  }

  /**
   * Looks up a user by their GitLab ID.
   *
   * @param token the access token
   * @param userId the user ID
   * @return the user or an empty optional if not found
   */
  public Optional<GitLabUser> findUserById(@NotNull String token, @NotNull Integer userId) {
    LOGGER.debug("Finding user with id: {}", userId);
    URL url = joinUrls(options.getRootUrl(),
        "/api/v4/users/" + userId.toString());
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);
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
   * @param token the access token
   * @param query the search string
   * @return the list of namespaces
   */
  public List<GitLabNamespace> findNamespaces(@NotNull String token, String query) {
    LOGGER.debug("Finding namespaces with query: {}", query);
    URL url = joinUrls(options.getRootUrl(),
        "/api/v4/namespaces" + (query != null ? "?search=" + query : ""));
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);
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
   * @param token the access token
   * @param namespaceId the namespace ID
   * @return the namespace or an empty optional if not found
   */
  public Optional<GitLabNamespace> findNamespaceById(@NotNull String token, @NotNull Integer namespaceId) {
    LOGGER.debug("Finding namespace with id: {}", namespaceId);
    URL url = joinUrls(options.getRootUrl(),
        "/api/v4/namespaces/" + namespaceId.toString());
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);
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
   * @param token the access token
   * @return the list of namespaces
   */
  public List<GitLabNamespace> findNamespaces(@NotNull String token) {
    return findNamespaces(token, null);
  }

  /**
   * Returns a list of public groups. The list can be filtered using a search string.
   *
   * @param token the access token
   * @param query the search string
   * @return the list of groups
   */
  public List<GitLabGroup> findGroups(@NotNull String token, String query) {
    LOGGER.debug("Finding groups with query: {}", query);
    URL url = joinUrls(options.getRootUrl(),
        "/api/v4/groups" + (query != null ? "?search=" + query : ""));
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);
    headers.set("Accept", "application/json");
    HttpEntity<?> request = new HttpEntity<>(headers);
    ResponseEntity<List<GitLabGroup>> response = restTemplate.exchange(
        url.toString(), HttpMethod.GET, request,
        new ParameterizedTypeReference<List<GitLabGroup>>() {});
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return response.getBody();
    } else {
      throw new StudyTrackerException("Failed to find groups in GitLab");
    }
  }

  /**
   * Returns a list of all public groups.
   *
   * @param token the access token
   * @return the list of groups
   */
  public List<GitLabGroup> findGroups(@NotNull String token) {
    return findGroups(token, null);
  }

  /**
   * Looks up a group by its ID.
   *
   * @param token the access token
   * @param groupId the group ID
   * @return the group or an empty optional if not found
   */
  public Optional<GitLabGroup> findGroupById(@NotNull String token, @NotNull Integer groupId) {
    LOGGER.debug("Finding group with id: {}", groupId);
    URL url = joinUrls(options.getRootUrl(),
        "/api/v4/groups/" + groupId.toString());
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);
    headers.set("Accept", "application/json");
    HttpEntity<?> request = new HttpEntity<>(headers);
    ResponseEntity<GitLabGroup> response = restTemplate.exchange(
        url.toString(), HttpMethod.GET, request, GitLabGroup.class);
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      return Optional.ofNullable(response.getBody());
    } else {
      return Optional.empty();
    }
  }

  public GitLabGroup createNewGroup(@NotNull String token, @NotNull GitLabNewGroupRequest newGroupRequest) {
    LOGGER.debug("Creating new group with name: {}", newGroupRequest.getName());
    URL url = joinUrls(options.getRootUrl(), "/api/v4/groups");
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/json");
    HttpEntity<GitLabNewGroupRequest> request = new HttpEntity<>(newGroupRequest, headers);
    ResponseEntity<GitLabGroup> response = restTemplate.exchange(
        url.toString(), HttpMethod.POST, request, GitLabGroup.class);
    if (response.getStatusCode().equals(HttpStatus.CREATED)) {
      return response.getBody();
    } else {
      throw new StudyTrackerException("Failed to create new group in GitLab");
    }
  }

  /**
   * Returns a filtered list of projects (aka. Git repositories).
   *
   * @param token the access token
   * @param query the search string
   * @return the list of projects
   */
  public List<GitLabProject> findProjects(@NotNull String token, String query) {
    LOGGER.debug("Finding projects with query: {}", query);
    URL url = joinUrls(options.getRootUrl(),
        "/api/v4/projects" + (query != null ? "?search=" + query : ""));
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);
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
   * @param token the access token
   * @return the list of projects
   */
  public List<GitLabProject> findProjects(@NotNull String token) {
    return findProjects(token, null);
  }

  /**
   * Creates a new GitLab project (aka. Git repository) for the given user.
   *
   * @param token the access token
   * @param user the user to assign as owner, if present
   * @param newProjectRequest the project creation request
   * @return
   */
  public GitLabProject createProject(
      @NotNull String token,
      @NotNull GitLabNewProjectRequest newProjectRequest,
      GitLabUser user
  ) {
    LOGGER.info("Creating project: {}", newProjectRequest);
    URL url = joinUrls(options.getRootUrl(), "/api/v4/projects"
        + (user != null ? "/user/" + user.getId().toString() : ""));
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);
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
   * @param token the access token
   * @param newProjectRequest the project creation request
   * @return
   */
  public GitLabProject createProject(
      @NotNull String token,
      @NotNull GitLabNewProjectRequest newProjectRequest
  ) {
    return createProject(token, newProjectRequest, null);
  }

  private URL joinUrls(URL root, String path) {
    try {
      return new URL(root, path);
    } catch (MalformedURLException ex) {
      throw new StudyTrackerException(ex);
    }
  }

  public RestTemplate getRestTemplate() {
    return restTemplate;
  }

  public GitLabOptions getOptions() {
    return options;
  }
}
