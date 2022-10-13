package io.studytracker.gitlab;

import java.util.Collection;

public interface GitLabOperations {

  Collection<Object> listUsers();

  Collection<Object> listGroups();

  Collection<Object> listProjects();

}
