package io.studytracker.gitlab;

import io.studytracker.git.GitGroup;
import io.studytracker.git.GitRepository;
import io.studytracker.git.GitUser;
import io.studytracker.gitlab.entities.GitLabGroup;
import io.studytracker.gitlab.entities.GitLabProject;
import io.studytracker.gitlab.entities.GitLabUser;

public class GitLabUtils {

  public static String getPathFromName(String name) {
    return name.toLowerCase()
        .replaceAll("[\\s_]+", "-")
        .replaceAll("[^a-z0-9]", "");
  }

  public static GitGroup toGitGroup(GitLabGroup group) {
    GitGroup gitGroup = new GitGroup();
    gitGroup.setGroupId(group.getId().toString());
    gitGroup.setParentGroupId(group.getParentId() != null ? group.getParentId().toString() : null);
    gitGroup.setName(group.getName());
    gitGroup.setPath(group.getPath());
    gitGroup.setDescription(group.getDescription());
    gitGroup.setCreatedAt(group.getCreatedAt());
    gitGroup.setWebUrl(group.getWebUrl());
    return gitGroup;
  }

  public static GitRepository toGitRepository(GitLabProject project) {
    GitRepository gitRepository = new GitRepository();
    gitRepository.setRepositoryId(project.getId().toString());
    gitRepository.setGroupId(project.getNamespace().getId().toString());
    gitRepository.setOwnerId(project.getOwner().getId().toString());
    gitRepository.setName(project.getName());
    gitRepository.setDescription(project.getDescription());
    gitRepository.setPath(project.getPath());
    gitRepository.setCreatedAt(project.getCreatedAt());
    gitRepository.setUpdatedAt(project.getLastActivityAt());
    gitRepository.setDefaultBranch(project.getDefaultBranch());
    gitRepository.setSshUrl(project.getSshUrlToRepo());
    gitRepository.setHttpUrl(project.getHttpUrlToRepo());
    gitRepository.setWebUrl(project.getWebUrl());
    return gitRepository;
  }

  public static GitUser toGitUser(GitLabUser user) {
    GitUser gitUser = new GitUser();
    gitUser.setUserId(user.getId().toString());
    gitUser.setUsername(user.getUsername());
    gitUser.setName(user.getName());
    gitUser.setEmail(user.getEmail());
    return gitUser;
  }

}
