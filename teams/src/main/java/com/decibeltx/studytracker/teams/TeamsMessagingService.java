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

import com.decibeltx.studytracker.core.model.Assay;
import com.decibeltx.studytracker.core.model.Message;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.service.MessagingService;
import com.decibeltx.studytracker.teams.entity.Channel;
import com.decibeltx.studytracker.teams.entity.DriveItem;
import com.decibeltx.studytracker.teams.entity.Team;
import com.decibeltx.studytracker.teams.entity.TeamsAuthentication;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

public class TeamsMessagingService implements MessagingService {

  public static final String CHANNEL_PROPERTY = "teamsChannel";
  public static final String TEAM_PROPERTY = "teamsTeam";

  @Autowired
  private TeamsBetaRestApiClient client;

  @Autowired
  private TeamsOptions options;

  private String getProgramTeamName(Program program) {
    String teamName = options.getDefaultTeam();
    if (program.getAttributes().containsKey(TEAM_PROPERTY)
        && StringUtils.hasText((String) program.getAttributes().get(TEAM_PROPERTY))) {
      teamName = (String) program.getAttributes().get(TEAM_PROPERTY);
    }
    return teamName;
  }

  private String getProgramChannelName(Program program) {
    String channelName = options.getDefaultChannel();
    if (program.getAttributes().containsKey(CHANNEL_PROPERTY)
        && StringUtils.hasText((String) program.getAttributes().get(CHANNEL_PROPERTY))) {
      channelName = (String) program.getAttributes().get(CHANNEL_PROPERTY);
    }
    return channelName;
  }

  private Team getProgramTeam(Program program, String token) {
    String teamName = getProgramTeamName(program);
    return getTeamByName(teamName, token);
  }

  private Team getTeamByName(String name, String token) {
    List<Team> teams = client.getJoinedTeamList(token);
    if (teams.isEmpty()) {
      throw new EntityNotFoundException("The authenticated user is not a member of any teams.");
    }
    Optional<Team> teamOptional = teams.stream()
        .filter(t -> t.getDisplayName().equals(name))
        .findFirst();
    if (!teamOptional.isPresent()) {
      throw new EntityNotFoundException("Cannot find team in list of joined teams: " + name);
    }
    return teamOptional.get();
  }

  private Channel getTeamChannelByName(Team team, String channelName, String token) {
    List<Channel> channels = client.getTeamChannels(team.getId(), token);
    Optional<Channel> channelOptional = channels.stream()
        .filter(c -> c.getDisplayName().equals(channelName))
        .findFirst();
    if (!channelOptional.isPresent()) {
      throw new EntityNotFoundException(
          "Channel does not exist in the target team: " + channelName);
    }
    return channelOptional.get();
  }

  private String getStudyFolderName(Study study) {
    return (study.getCode() + "_" + study.getName()).replaceAll("[^\\w_-]", "_");
  }

  public DriveItem uploadStudyFile(Study study, Resource resource) {
    Program program = study.getProgram();
    TeamsAuthentication authentication = client.authenticate();
    Team team = getProgramTeam(program, authentication.getAccessToken());
    Channel channel = getTeamChannelByName(team, getProgramChannelName(program),
        authentication.getAccessToken());
    DriveItem drive = client
        .getTeamsChannelDriveFolder(team.getId(), channel.getId(), authentication.getAccessToken());
    DriveItem studyFolder = client.createChannelDriveFolder(getStudyFolderName(study),
        drive.getParentReference().getDriveId(), drive.getId(), authentication.getAccessToken(),
        true);

    List<DriveItem> folderContents = client.getChannelDriveFolderContents(
        studyFolder.getParentReference().getDriveId(), studyFolder.getId(),
        authentication.getAccessToken());
    if (!folderContents.isEmpty()) {
      Optional<DriveItem> optional = folderContents.stream()
          .filter(i -> i.getName().equals(resource.getFilename()))
          .findFirst();
      if (optional.isPresent()) {
        DriveItem fileItem = optional.get();
        return client.updateFileInDrive(resource, studyFolder.getParentReference().getDriveId(),
            fileItem.getId(), authentication.getAccessToken());
      }
    }

    return client.uploadFileToDrive(resource,
        studyFolder.getParentReference().getDriveId(), studyFolder.getId(),
        authentication.getAccessToken());
  }

  @Override
  public Message sendStudyMessage(String content, Study study) {
    Program program = study.getProgram();
    return sendProgramMessage(content, program);
  }

  @Override
  public Message sendAssayMessage(String content, Assay assay) {
    Study study = assay.getStudy();
    return sendStudyMessage(content, study);
  }

  @Override
  public Message sendMessage(String content) {
    TeamsAuthentication authentication = client.authenticate();
    Team team = getTeamByName(options.getDefaultTeam(), authentication.getAccessToken());
    Channel channel = getTeamChannelByName(team, options.getDefaultChannel(),
        authentication.getAccessToken());
    return client.postMessageToChannel(content, team.getId(), channel.getId(),
        authentication.getAccessToken());
  }

  @Override
  public Message sendProgramMessage(String content, Program program) {
    TeamsAuthentication authentication = client.authenticate();
    Team team = getProgramTeam(program, authentication.getAccessToken());
    Channel channel = getTeamChannelByName(team, getProgramChannelName(program),
        authentication.getAccessToken());
    return client.postMessageToChannel(content, team.getId(), channel.getId(),
        authentication.getAccessToken());
  }
}
