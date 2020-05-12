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

package com.decibeltx.studytracker.teams;

import com.decibeltx.studytracker.teams.entity.Channel;
import com.decibeltx.studytracker.teams.entity.ChatMessage;
import com.decibeltx.studytracker.teams.entity.DriveItem;
import com.decibeltx.studytracker.teams.entity.ObjectList;
import com.decibeltx.studytracker.teams.entity.Team;
import com.decibeltx.studytracker.teams.entity.TeamsAuthentication;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class TeamsBetaRestApiClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(TeamsBetaRestApiClient.class);

  private final RestTemplate restTemplate;
  private final TeamsOptions configuration;

  public TeamsBetaRestApiClient(TeamsOptions configuration, RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
    this.configuration = configuration;
  }

  public TeamsBetaRestApiClient(TeamsOptions configuration) {
    this.configuration = configuration;
    this.restTemplate = new RestTemplate();
  }

  /**
   * Authenticates with the MS Graph service and returns an authentication token object.
   *
   * @return
   */
  public TeamsAuthentication authenticate() {

    LOGGER.info(String
        .format("Authenticating user %s with Microsoft Teams API.", configuration.getUsername()));

    HttpHeaders headers = new HttpHeaders();
    headers.set("Connection", "keep-alive");
    headers.set("Content-Length", "251");
    headers.set("Accept-Encoding", "gzip, deflate");
    headers.set("Host", "login.microsoftonline.com");
    headers.set("Cache-Control", "no-cache");
    headers.set("Content-Type", "application/x-www-form-urlencoded");
    headers.set("Accept", "*/*");

    MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
    data.set("client_id", configuration.getClientId());
    data.set("scope", "user.read openid profile offline_access");
    data.set("client_secret", configuration.getSecret());
    data.set("username", configuration.getUsername());
    data.set("password", configuration.getPassword());
    data.set("grant_type", "password");

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(data, headers);

    ResponseEntity<TeamsAuthentication> response = restTemplate.exchange(
        "https://login.microsoftonline.com/organizations/oauth2/v2.0/token",
        HttpMethod.POST,
        request,
        TeamsAuthentication.class);
    if (response.getStatusCode() != HttpStatus.OK) {
      throw new TeamsException("Unable to authenticate user: " + configuration.getUsername());
    }

    return response.getBody();

  }

  /**
   * @param token
   * @return
   */
  public List<Team> getJoinedTeamList(String token) {

    LOGGER.info(
        String.format("Getting list of joined teams for user %s", configuration.getUsername()));
    HttpEntity<MultiValueMap<String, Object>> request
        = new HttpEntity<>(null, getStandardHeaders(token));
    String url = "https://graph.microsoft.com/beta/me/joinedTeams";
    ResponseEntity<ObjectList<Team>> response = restTemplate.exchange(url, HttpMethod.GET, request,
        new ParameterizedTypeReference<ObjectList<Team>>() {
        });
    ObjectList<Team> teamList = response.getBody();
    if (response.getStatusCode() != HttpStatus.OK || teamList == null
        || teamList.getValues() == null) {
      throw new TeamsException("Failed to fetch list of joined teams.");
    }
    return teamList.getValues();

  }

  /**
   * @param teamId
   * @param token
   * @return
   */
  public List<Channel> getTeamChannels(String teamId, String token) {

    LOGGER.info(String.format("Getting list of channels for team %s", teamId));
    HttpEntity<MultiValueMap<String, Object>> request
        = new HttpEntity<>(null, getStandardHeaders(token));
    String url = "https://graph.microsoft.com/beta/teams/" + teamId + "/channels";
    ResponseEntity<ObjectList<Channel>> response = restTemplate.exchange(url, HttpMethod.GET,
        request, new ParameterizedTypeReference<ObjectList<Channel>>() {
        });
    ObjectList<Channel> channelList = response.getBody();
    if (response.getStatusCode() != HttpStatus.OK || channelList == null
        || channelList.getValues() == null) {
      throw new TeamsException("Failed to fetch list of team channels.");
    }
    return channelList.getValues();

  }

  /**
   * Fetches all messages posted in the given channel. Currently, this defaults to 20 messages.
   *
   * @param teamId
   * @param channelId
   * @param token
   * @return
   */
  public List<ChatMessage> getChannelMessages(String teamId, String channelId, String token) {

    LOGGER.info(String.format("Getting messages from channel %s", channelId));
    HttpEntity<MultiValueMap<String, Object>> request
        = new HttpEntity<>(null, getStandardHeaders(token));
    String url =
        "https://graph.microsoft.com/beta/teams/" + teamId + "/channels/" + channelId + "/messages";
    ResponseEntity<ObjectList<ChatMessage>> response = restTemplate.exchange(url, HttpMethod.GET,
        request, new ParameterizedTypeReference<ObjectList<ChatMessage>>() {
        });
    ObjectList<ChatMessage> messageList = response.getBody();
    if (response.getStatusCode() != HttpStatus.OK || messageList == null
        || messageList.getValues() == null) {
      throw new TeamsException("Failed to fetch messages.");
    }
    return messageList.getValues();

  }

  /**
   * Posts and HTML-formatted message to the Teams channel identified by the {@code teamId} and
   * {@code channelId} provided.
   *
   * @param message
   * @param teamId
   * @param channelId
   * @param token
   * @return
   */
  public ChatMessage postMessageToChannel(String message, String teamId, String channelId,
      String token) {
    LOGGER.info(String.format("Posting message to channel %s", channelId));
    Map<String, Object> payload = new LinkedHashMap<>();
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("contentType", "html");
    body.put("content", message);
    payload.put("body", body);

    HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, getStandardHeaders(token));
    String url = "https://graph.microsoft.com/beta/teams/" + teamId + "/channels/"
        + channelId + "/messages";
    ResponseEntity<ChatMessage> response = restTemplate
        .exchange(url, HttpMethod.POST, request, ChatMessage.class);
    if (response.getStatusCode() != HttpStatus.CREATED || response.getBody() == null) {
      throw new TeamsException("Failed to post message to channel.");
    }
    return response.getBody();

  }

  /**
   * Fetches reference to a channel's Drive folder.
   *
   * @param teamId
   * @param channelId
   * @param token
   * @return
   */
  public DriveItem getTeamsChannelDriveFolder(String teamId, String channelId, String token) {
    LOGGER.info(String.format("Getting drive folder for channel: %s", channelId));
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(null,
        getStandardHeaders(token));
    String url = "https://graph.microsoft.com/beta/teams/" + teamId + "/channels/"
        + channelId + "/filesFolder";
    ResponseEntity<DriveItem> response = restTemplate
        .exchange(url, HttpMethod.GET, request, DriveItem.class);
    if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
      throw new TeamsException("Failed to fetch channel drive folder.");
    }
    return response.getBody();
  }

  /**
   * @param folderName
   * @param channelDriveId
   * @param channelDriveItemId
   * @param token
   * @return
   */
  public DriveItem createChannelDriveFolder(String folderName, String channelDriveId,
      String channelDriveItemId, String token) {
    return this
        .createChannelDriveFolder(folderName, channelDriveId, channelDriveItemId, token, false);
  }

  /**
   * @param folderName
   * @param channelDriveId
   * @param channelDriveItemId
   * @param token
   * @param overwrite
   * @return
   */
  public DriveItem createChannelDriveFolder(String folderName, String channelDriveId,
      String channelDriveItemId, String token, boolean overwrite) {

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("name", folderName);
    body.put("folder", new HashMap<String, Object>());
    body.put("@microsoft.graph.conflictBehavior", overwrite ? "replace" : "fail");

    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, getStandardHeaders(token));
    String url = "https://graph.microsoft.com/beta/drives/" + channelDriveId + "/items/"
        + channelDriveItemId + "/children";
    ResponseEntity<DriveItem> response
        = restTemplate.exchange(url, HttpMethod.POST, request, DriveItem.class);
    return response.getBody();

  }

  /**
   * @param channelDriveId
   * @param channelDriveItemId
   * @param token
   * @return
   */
  public List<DriveItem> getChannelDriveFolderContents(String channelDriveId,
      String channelDriveItemId, String token) {
    HttpEntity<Map<String, Object>> request = new HttpEntity<>(null, getStandardHeaders(token));
    String url = "https://graph.microsoft.com/beta/drives/" + channelDriveId + "/items/"
        + channelDriveItemId + "/children";
    ResponseEntity<ObjectList<DriveItem>> response
        = restTemplate.exchange(url, HttpMethod.GET, request,
        new ParameterizedTypeReference<ObjectList<DriveItem>>() {
        });
    if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
      throw new TeamsException("Failed to fetch drive contents");
    }
    return response.getBody().getValues();
  }

  public DriveItem uploadFileToDrive(Resource resource, String driveId, String itemId,
      String token) {

    LOGGER.info(String.format("Uploading file to Teams drive: %s", resource.getFilename()));
    HttpHeaders headers = new HttpHeaders();
    headers.set("Host", "graph.microsoft.com");
    headers.set("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE);
    headers.set("Authorization", token);
    headers.set("Accept", "application/json");

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.set("file", resource);
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
    String url = "https://graph.microsoft.com/beta/drives/" + driveId + "/items/"
        + itemId + ":/" + resource.getFilename() + ":/content";
    ResponseEntity<DriveItem> response = restTemplate
        .exchange(url, HttpMethod.PUT, request, DriveItem.class);

    if (response.getStatusCode() != HttpStatus.CREATED || response.getBody() == null) {
      throw new TeamsException("Failed to upload file to Teams.");
    }

    return response.getBody();

  }

  public DriveItem updateFileInDrive(Resource resource, String driveId, String fileItemId,
      String token) {
    LOGGER.info(String.format("Updating existing file in Teams drive: %s", resource.getFilename()));
    HttpHeaders headers = new HttpHeaders();
    headers.set("Host", "graph.microsoft.com");
    headers.set("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE);
    headers.set("Authorization", token);
    headers.set("Accept", "application/json");

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.set("file", resource);
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
    String url = "https://graph.microsoft.com/beta/drives/" + driveId + "/items/"
        + fileItemId + "/content";
    ResponseEntity<DriveItem> response = restTemplate
        .exchange(url, HttpMethod.PUT, request, DriveItem.class);

    if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
      throw new TeamsException("Failed to upload file to Teams.");
    }

    return response.getBody();
  }

  private HttpHeaders getStandardHeaders(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Host", "graph.microsoft.com");
    headers.set("Content-Type", "application/json");
    headers.set("Authorization", token);
    headers.set("Accept", "application/json");
    return headers;
  }

}
