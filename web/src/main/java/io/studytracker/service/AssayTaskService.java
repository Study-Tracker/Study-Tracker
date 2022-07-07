package io.studytracker.service;

import io.studytracker.model.Assay;
import io.studytracker.model.AssayTask;
import io.studytracker.repository.AssayRepository;
import io.studytracker.repository.AssayTaskRepository;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssayTaskService {

  @Autowired private AssayRepository assayRepository;

  @Autowired private AssayTaskRepository assayTaskRepository;

  public List<AssayTask> findAssayTasks(Assay assay) {
    return this.findAssayTasks(assay.getId());
  }

  public List<AssayTask> findAssayTasks(Long id) {
    return assayTaskRepository.findByAssayId(id);
  }

  @Transactional
  public void addAssayTask(AssayTask task, Assay assay) {
    if (task.getOrder() == null) {
      task.setOrder(assay.getTasks().size());
    }
    task.setAssay(assay);
    assay.addTask(task);
    assayRepository.save(assay);
  }

  @Transactional
  public void updateAssayTask(AssayTask task, Assay assay) {
    AssayTask t = assayTaskRepository.getById(task.getId());
    t.setAssay(assay);
    t.setStatus(task.getStatus());
    t.setOrder(task.getOrder());
    t.setLabel(task.getLabel());
    assayTaskRepository.save(t);
    Assay a = assayRepository.getById(assay.getId());
    a.setUpdatedAt(new Date());
    assayRepository.save(a);
  }

  @Transactional
  public void deleteAssayTask(AssayTask task, Assay assay) {
    assay.removeTask(task.getId());
    assayRepository.save(assay);
  }
}
