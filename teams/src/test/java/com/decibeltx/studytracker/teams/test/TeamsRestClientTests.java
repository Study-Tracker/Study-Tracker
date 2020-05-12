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

package com.decibeltx.studytracker.teams.test;

import com.decibeltx.studytracker.teams.TeamsBetaRestApiClient;
import com.decibeltx.studytracker.teams.entity.Channel;
import com.decibeltx.studytracker.teams.entity.ChatMessage;
import com.decibeltx.studytracker.teams.entity.DriveItem;
import com.decibeltx.studytracker.teams.entity.Team;
import com.decibeltx.studytracker.teams.entity.TeamsAuthentication;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles({"example"})
public class TeamsRestClientTests {

  @Autowired
  private Environment env;

  @Autowired
  private TeamsBetaRestApiClient client;

  @Test
  public void authenticationTest() {
    TeamsAuthentication authentication = client.authenticate();
    System.out.println(authentication.toString());
    Assert.assertNotNull(authentication);
    Assert.assertNotNull(authentication.getAccessToken());
  }

  @Test
  public void listJoinedTeamsTest() {
    TeamsAuthentication authentication = client.authenticate();
    List<Team> teams = client.getJoinedTeamList(authentication.getAccessToken());
    Assert.assertNotNull(teams);
    Assert.assertTrue(!teams.isEmpty());
    Team team = teams.get(0);
    Assert.assertNotNull(team);
    Assert.assertNotNull(team.getId());
    Assert.assertNotNull(team.getDisplayName());
    System.out.println(team.toString());
  }

  @Test
  public void listTeamChannelsTest() {
    TeamsAuthentication authentication = client.authenticate();
    List<Team> teams = client.getJoinedTeamList(authentication.getAccessToken());
    Assert.assertNotNull(teams);
    Assert.assertTrue(!teams.isEmpty());
    Team team = teams.stream()
        .filter(t -> t.getDisplayName().equals(env.getRequiredProperty("teams.test.team")))
        .collect(Collectors.toList())
        .get(0);
    Assert.assertNotNull(team);
    Assert.assertEquals(env.getRequiredProperty("teams.test.team"), team.getDisplayName());
    List<Channel> channels = client.getTeamChannels(team.getId(), authentication.getAccessToken());
    Assert.assertNotNull(channels);
    Assert.assertTrue(!channels.isEmpty());
    System.out.println(channels.toString());
  }

  @Test
  public void listChannelMessagesTest() {
    TeamsAuthentication authentication = client.authenticate();
    List<Team> teams = client.getJoinedTeamList(authentication.getAccessToken());
    Assert.assertNotNull(teams);
    Assert.assertTrue(!teams.isEmpty());
    Team team = teams.stream()
        .filter(t -> t.getDisplayName().equals(env.getRequiredProperty("teams.test.team")))
        .collect(Collectors.toList())
        .get(0);
    Assert.assertNotNull(team);
    Assert.assertEquals(env.getRequiredProperty("teams.test.team"), team.getDisplayName());
    List<Channel> channels = client.getTeamChannels(team.getId(), authentication.getAccessToken());
    Assert.assertNotNull(channels);
    Assert.assertTrue(!channels.isEmpty());
    Channel channel = channels.stream()
        .filter(c -> c.getDisplayName().equals(env.getRequiredProperty("teams.test.channel")))
        .collect(Collectors.toList())
        .get(0);
    Assert.assertNotNull(channel);
    List<ChatMessage> messages = client.getChannelMessages(team.getId(), channel.getId(),
        authentication.getAccessToken());
    Assert.assertNotNull(messages);
    Assert.assertTrue(!messages.isEmpty());
    System.out.println(messages.toString());
  }

  @Test
  public void postMessageToTeamsTest() {
    TeamsAuthentication authentication = client.authenticate();
    List<Team> teams = client.getJoinedTeamList(authentication.getAccessToken());
    Assert.assertNotNull(teams);
    Assert.assertTrue(!teams.isEmpty());
    Team team = teams.stream()
        .filter(t -> t.getDisplayName().equals(env.getRequiredProperty("teams.test.team")))
        .collect(Collectors.toList())
        .get(0);
    Assert.assertNotNull(team);
    Assert.assertEquals(env.getRequiredProperty("teams.test.team"), team.getDisplayName());
    List<Channel> channels = client.getTeamChannels(team.getId(), authentication.getAccessToken());
    Assert.assertNotNull(channels);
    Assert.assertTrue(!channels.isEmpty());
    Channel channel = channels.stream()
        .filter(c -> c.getDisplayName().equals(env.getRequiredProperty("teams.test.channel")))
        .collect(Collectors.toList())
        .get(0);
    Assert.assertNotNull(channel);
    String body = "This is a test";
    ChatMessage message = client.postMessageToChannel(body, team.getId(), channel.getId(),
        authentication.getAccessToken());
    Assert.assertNotNull(message);
    System.out.println(message.toString());
  }

  @Test
  public void getChannelFolderTest() {
    TeamsAuthentication authentication = client.authenticate();
    List<Team> teams = client.getJoinedTeamList(authentication.getAccessToken());
    Assert.assertNotNull(teams);
    Assert.assertTrue(!teams.isEmpty());
    Team team = teams.stream()
        .filter(t -> t.getDisplayName().equals(env.getRequiredProperty("teams.test.team")))
        .collect(Collectors.toList())
        .get(0);
    Assert.assertNotNull(team);
    Assert.assertEquals(env.getRequiredProperty("teams.test.team"), team.getDisplayName());
    List<Channel> channels = client.getTeamChannels(team.getId(), authentication.getAccessToken());
    Assert.assertNotNull(channels);
    Assert.assertTrue(!channels.isEmpty());
    Channel channel = channels.stream()
        .filter(c -> c.getDisplayName().equals(env.getRequiredProperty("teams.test.channel")))
        .collect(Collectors.toList())
        .get(0);
    Assert.assertNotNull(channel);
    DriveItem folder = client
        .getTeamsChannelDriveFolder(team.getId(), channel.getId(), authentication.getAccessToken());
    Assert.assertNotNull(folder);
    Assert.assertNotNull(folder.getId());
    System.out.println(folder.toString());
  }

  @Test
  public void createChannelFolderTest() {
    TeamsAuthentication authentication = client.authenticate();
    List<Team> teams = client.getJoinedTeamList(authentication.getAccessToken());
    Assert.assertNotNull(teams);
    Assert.assertTrue(!teams.isEmpty());
    Team team = teams.stream()
        .filter(t -> t.getDisplayName().equals(env.getRequiredProperty("teams.test.team")))
        .collect(Collectors.toList())
        .get(0);
    Assert.assertNotNull(team);
    Assert.assertEquals(env.getRequiredProperty("teams.test.team"), team.getDisplayName());
    List<Channel> channels = client.getTeamChannels(team.getId(), authentication.getAccessToken());
    Assert.assertNotNull(channels);
    Assert.assertTrue(!channels.isEmpty());
    Channel channel = channels.stream()
        .filter(c -> c.getDisplayName().equals(env.getRequiredProperty("teams.test.channel")))
        .collect(Collectors.toList())
        .get(0);
    Assert.assertNotNull(channel);
    DriveItem folder = client
        .getTeamsChannelDriveFolder(team.getId(), channel.getId(), authentication.getAccessToken());
    Assert.assertNotNull(folder);
    String itemId = folder.getId();
    String driveId = folder.getParentReference().getDriveId();
    Assert.assertNotNull(itemId);
    Assert.assertNotNull(driveId);
    DriveItem newFolder = client
        .createChannelDriveFolder("Test Folder", driveId, itemId, authentication.getAccessToken(),
            true);
    Assert.assertNotNull(newFolder);
    Assert.assertNotNull(newFolder.getId());
    Assert.assertEquals("Test Folder", newFolder.getName());
  }

  @Test
  public void readFolderContentsTest() {
    TeamsAuthentication authentication = client.authenticate();
    List<Team> teams = client.getJoinedTeamList(authentication.getAccessToken());
    Assert.assertNotNull(teams);
    Assert.assertTrue(!teams.isEmpty());
    Team team = teams.stream()
        .filter(t -> t.getDisplayName().equals(env.getRequiredProperty("teams.test.team")))
        .collect(Collectors.toList())
        .get(0);
    Assert.assertNotNull(team);
    Assert.assertEquals(env.getRequiredProperty("teams.test.team"), team.getDisplayName());
    List<Channel> channels = client.getTeamChannels(team.getId(), authentication.getAccessToken());
    Assert.assertNotNull(channels);
    Assert.assertTrue(!channels.isEmpty());
    Channel channel = channels.stream()
        .filter(c -> c.getDisplayName().equals(env.getRequiredProperty("teams.test.channel")))
        .collect(Collectors.toList())
        .get(0);
    Assert.assertNotNull(channel);
    DriveItem folder = client
        .getTeamsChannelDriveFolder(team.getId(), channel.getId(), authentication.getAccessToken());
    Assert.assertNotNull(folder);
    String itemId = folder.getId();
    String driveId = folder.getParentReference().getDriveId();
    Assert.assertNotNull(itemId);
    Assert.assertNotNull(driveId);
    List<DriveItem> items = client
        .getChannelDriveFolderContents(driveId, itemId, authentication.getAccessToken());
    Assert.assertNotNull(items);
  }

  // This will fail if the file already exists
  @Test
  public void uploadFileTest() {

    TeamsAuthentication authentication = client.authenticate();

    List<Team> teams = client.getJoinedTeamList(authentication.getAccessToken());
    Assert.assertNotNull(teams);
    Assert.assertTrue(!teams.isEmpty());
    Team team = teams.stream()
        .filter(t -> t.getDisplayName().equals(env.getRequiredProperty("teams.test.team")))
        .collect(Collectors.toList())
        .get(0);
    Assert.assertNotNull(team);
    Assert.assertEquals(env.getRequiredProperty("teams.test.team"), team.getDisplayName());

    List<Channel> channels = client.getTeamChannels(team.getId(), authentication.getAccessToken());
    Assert.assertNotNull(channels);
    Assert.assertTrue(!channels.isEmpty());
    Channel channel = channels.stream()
        .filter(c -> c.getDisplayName().equals(env.getRequiredProperty("teams.test.channel")))
        .collect(Collectors.toList())
        .get(0);
    Assert.assertNotNull(channel);

    DriveItem folder = client
        .getTeamsChannelDriveFolder(team.getId(), channel.getId(), authentication.getAccessToken());
    Assert.assertNotNull(folder);
    String itemId = folder.getId();
    String driveId = folder.getParentReference().getDriveId();
    Assert.assertNotNull(itemId);
    Assert.assertNotNull(driveId);

    DriveItem newFile = null;
    Resource resource = new ClassPathResource("test.txt");

    List<DriveItem> folderContents = client.getChannelDriveFolderContents(
        folder.getParentReference().getDriveId(), folder.getId(),
        authentication.getAccessToken());
    if (!folderContents.isEmpty()) {
      Optional<DriveItem> optional = folderContents.stream()
          .filter(i -> i.getName().equals(resource.getFilename()))
          .findFirst();
      if (optional.isPresent()) {
        DriveItem fileItem = optional.get();
        newFile = client.updateFileInDrive(resource, folder.getParentReference().getDriveId(),
            fileItem.getId(), authentication.getAccessToken());
      }
    }

    if (newFile == null) {
      newFile = client.uploadFileToDrive(new ClassPathResource("test.txt"), driveId, itemId,
          authentication.getAccessToken());
    }

    Assert.assertNotNull(newFile);
    Assert.assertEquals("test.txt", newFile.getName());

  }

}
