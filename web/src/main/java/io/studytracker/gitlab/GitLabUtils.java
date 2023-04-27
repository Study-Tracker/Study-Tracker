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

import io.studytracker.git.GitServerGroup;
import io.studytracker.git.GitServerRepository;
import io.studytracker.git.GitServerUser;
import io.studytracker.gitlab.entities.GitLabProject;
import io.studytracker.gitlab.entities.GitLabProjectGroup;
import io.studytracker.gitlab.entities.GitLabUser;
import io.studytracker.model.Assay;
import io.studytracker.model.Study;

public class GitLabUtils {

  public static String getPathFromName(String name) {
    return name.toLowerCase()
        .replaceAll("[\\s_]+", "-")
        .replaceAll("[^a-z0-9-]", "");
  }

  public static String getStudyProjectName(Study study) {
    return study.getCode() + " - " + study.getName().replaceAll("[^\\w\\d\\s_+.]", "");
  }

  public static String getStudyProjectPath(Study study) {
    return getPathFromName(study.getCode());
  }

  public static String getAssayProjectName(Assay assay) {
    return assay.getCode() + " - " + assay.getName().replaceAll("[^\\w\\d\\s_+.]", "");
  }

  public static String getAssayProjectPath(Assay assay) {
    return getPathFromName(assay.getCode());
  }

  public static GitServerGroup toGitServerGroup(GitLabProjectGroup group) {
    GitServerGroup gitServerGroup = new GitServerGroup();
    gitServerGroup.setGroupId(group.getId().toString());
    gitServerGroup.setParentGroupId(group.getParentId() != null ? group.getParentId().toString() : null);
    gitServerGroup.setName(group.getName());
    gitServerGroup.setPath(group.getPath());
    gitServerGroup.setDescription(group.getDescription());
    gitServerGroup.setCreatedAt(group.getCreatedAt());
    gitServerGroup.setWebUrl(group.getWebUrl());
    return gitServerGroup;
  }

  public static GitServerRepository toGitServerRepository(GitLabProject project) {
    GitServerRepository gitServerRepository = new GitServerRepository();
    gitServerRepository.setRepositoryId(project.getId().toString());
    gitServerRepository.setGroupId(project.getNamespace().getId().toString());
    gitServerRepository.setOwnerId(project.getOwner() != null ? project.getOwner().getId().toString() : null);
    gitServerRepository.setName(project.getName());
    gitServerRepository.setDescription(project.getDescription());
    gitServerRepository.setPath(project.getPath());
    gitServerRepository.setCreatedAt(project.getCreatedAt());
    gitServerRepository.setUpdatedAt(project.getLastActivityAt());
    gitServerRepository.setDefaultBranch(project.getDefaultBranch());
    gitServerRepository.setSshUrl(project.getSshUrlToRepo());
    gitServerRepository.setHttpUrl(project.getHttpUrlToRepo());
    gitServerRepository.setWebUrl(project.getWebUrl());
    return gitServerRepository;
  }

  public static GitServerUser toGitServerUser(GitLabUser user) {
    GitServerUser gitUser = new GitServerUser();
    gitUser.setUserId(user.getId().toString());
    gitUser.setUsername(user.getUsername());
    gitUser.setName(user.getName());
    gitUser.setEmail(user.getEmail());
    return gitUser;
  }

}
