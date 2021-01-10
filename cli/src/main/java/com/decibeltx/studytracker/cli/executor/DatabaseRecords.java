package com.decibeltx.studytracker.cli.executor;

import com.decibeltx.studytracker.core.model.Collaborator;
import com.decibeltx.studytracker.core.model.Keyword;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.model.User;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class DatabaseRecords {

  private List<Program> programs = new ArrayList<>();

  private List<User> users = new ArrayList<>();

  private List<Collaborator> collaborators = new ArrayList<>();

  private List<Keyword> keywords = new ArrayList<>();

  private List<Study> studies = new ArrayList<>();

}
