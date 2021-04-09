package com.decibeltx.studytracker.controller.api;

import com.decibeltx.studytracker.controller.UserAuthenticationUtils;
import com.decibeltx.studytracker.events.util.AssayActivityUtils;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.model.Activity;
import com.decibeltx.studytracker.model.Assay;
import com.decibeltx.studytracker.model.Task;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.service.AssayTaskService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/assay/{assayId}/tasks", "/api/study/{studyId}/assays/{assayId}/tasks"})
public class AssayTasksController extends AbstractAssayController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AssayTasksController.class);

  @Autowired
  private AssayTaskService assayTaskService;

  @GetMapping("")
  public List<Task> fetchTasks(@PathVariable("assayId") String assayId) {
    return this.getAssayFromIdentifier(assayId).getTasks();
  }

  @PostMapping("")
  public HttpEntity<Task> addTask(@PathVariable("assayId") String assayId, @RequestBody Task task) {

    LOGGER.info("Adding new task to assay: " + task.toString());
    Assay assay = this.getAssayFromIdentifier(assayId);

    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);
    assay.setLastModifiedBy(user);

    assayTaskService.addAssayTask(task, assay);

    Activity activity = AssayActivityUtils.fromTaskAdded(assay, user, task);
    this.getActivityService().create(activity);
    this.getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(task, HttpStatus.OK);

  }

  @PutMapping("")
  public HttpEntity<Task> updateTask(@PathVariable("assayId") String assayId,
      @RequestBody Task task) {

    LOGGER.info("Updating task: " + task.toString());

    Assay assay = this.getAssayFromIdentifier(assayId);

    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);
    assay.setLastModifiedBy(user);

    assayTaskService.updateAssayTask(task, assay);

    Activity activity = AssayActivityUtils.fromTaskUpdate(assay, user, task);
    this.getActivityService().create(activity);
    this.getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(task, HttpStatus.OK);

  }

  @DeleteMapping("")
  public HttpEntity<?> removeTask(@PathVariable("assayId") String assayId, @RequestBody Task task) {

    LOGGER.info("Deleting task: " + task.toString());

    Assay assay = this.getAssayFromIdentifier(assayId);

    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);
    assay.setLastModifiedBy(user);

    assayTaskService.deleteAssayTask(task, assay);

    Activity activity = AssayActivityUtils.fromTaskDeleted(assay, user, task);
    this.getActivityService().create(activity);
    this.getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(HttpStatus.OK);

  }

}
