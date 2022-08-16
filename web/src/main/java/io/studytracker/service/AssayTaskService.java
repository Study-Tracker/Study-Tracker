package io.studytracker.service;

import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayTask;
import io.studytracker.repository.AssayRepository;
import io.studytracker.repository.AssayTaskRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssayTaskService {

  @Autowired
  private AssayRepository assayRepository;

  @Autowired
  private AssayTaskRepository assayTaskRepository;

  public Optional<AssayTask> findById(Long id) {
    return assayTaskRepository.findById(id);
  }

  public Page<AssayTask> findAll(Pageable pageable) {
    return assayTaskRepository.findAll(pageable);
  }

  public Page<AssayTask> findAssayTasks(Long id, Pageable pageable) {
    return assayTaskRepository.findByAssayId(id, pageable);
  }

  public Page<AssayTask> findAssayTasks(Assay assay, Pageable pageable) {
    return this.findAssayTasks(assay.getId(), pageable);
  }

  public List<AssayTask> findAssayTasks(Assay assay) {
    return this.findAssayTasks(assay.getId());
  }

  public List<AssayTask> findAssayTasks(Long id) {
    return assayTaskRepository.findByAssayId(id);
  }

  @Transactional
  public AssayTask addAssayTask(AssayTask task, Assay assay) {
    if (task.getOrder() == null) {
      task.setOrder(assay.getTasks().size());
    }
    task.setAssay(assay);
    return assayTaskRepository.save(task);
//    assay.addTask(task);
//    assayRepository.save(assay);
//    return task;
  }

  @Transactional
  public AssayTask updateAssayTask(AssayTask task, Assay assay) {
    AssayTask t = assayTaskRepository.getById(task.getId());
    t.setAssay(assay);
    t.setStatus(task.getStatus());
    t.setOrder(task.getOrder());
    t.setLabel(task.getLabel());
    assayTaskRepository.save(t);
    Assay a = assayRepository.getById(assay.getId());
    a.setUpdatedAt(new Date());
    assayRepository.save(a);
    return assayTaskRepository.findById(task.getId())
        .orElseThrow(() -> new RecordNotFoundException("Cannot find assay task: " + task.getId()));
  }

  @Transactional
  public void deleteAssayTask(AssayTask task, Assay assay) {
    assay.removeTask(task.getId());
    assayRepository.save(assay);
  }
}
