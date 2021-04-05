package com.decibeltx.studytracker.service.impl;

import com.decibeltx.studytracker.model.Assay;
import com.decibeltx.studytracker.model.Task;
import com.decibeltx.studytracker.repository.AssayRepository;
import com.decibeltx.studytracker.service.AssayTaskService;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssayTaskServiceImpl implements AssayTaskService {

  @Autowired
  private AssayRepository assayRepository;

  @Override
  public List<Task> findAssayTasks(Assay assay) {
    return assay.getTasks();
  }

  @Override
  public void addAssayTask(Task task, Assay assay) {
    Date now = new Date();
    task.setCreatedAt(now);
    task.setUpdatedAt(now);
    if (task.getOrder() == null) {
      task.setOrder(assay.getTasks().size());
    }
    assay.getTasks().add(task);
    assayRepository.save(assay);
  }

  @Override
  public void updateAssayTask(Task task, Assay assay) {
    for (Task t : assay.getTasks()) {
      if (t.getLabel().equals(task.getLabel())) {
        t.setStatus(task.getStatus());
        t.setUpdatedAt(new Date());
      }
    }
    assayRepository.save(assay);
  }

  @Override
  public void deleteAssayTask(Task task, Assay assay) {
    List<Task> tasks = assay.getTasks().stream()
        .filter(t -> !t.getLabel().equals(task.getLabel()))
        .collect(Collectors.toList());
    assay.setTasks(tasks);
    assayRepository.save(assay);
  }
}
