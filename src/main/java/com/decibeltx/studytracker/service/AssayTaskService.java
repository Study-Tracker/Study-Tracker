package com.decibeltx.studytracker.service;

import com.decibeltx.studytracker.model.Assay;
import com.decibeltx.studytracker.model.Task;
import java.util.List;

public interface AssayTaskService {

  List<Task> findAssayTasks(Assay assay);

  void addAssayTask(Task task, Assay assay);

  void updateAssayTask(Task task, Assay assay);

  void deleteAssayTask(Task task, Assay assay);

}
