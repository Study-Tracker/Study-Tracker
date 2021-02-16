package com.decibeltx.studytracker.core.service;

import com.decibeltx.studytracker.core.model.Assay;
import com.decibeltx.studytracker.core.model.Task;
import java.util.List;

public interface AssayTaskService {

  List<Task> findAssayTasks(Assay assay);

  void addAssayTask(Task task, Assay assay);

  void updateAssayTask(Task task, Assay assay);

  void deleteAssayTask(Task task, Assay assay);

}
