package io.studytracker.controller.api;

import io.studytracker.controller.UserAuthenticationUtils;
import io.studytracker.events.util.AssayActivityUtils;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.AssayTaskDto;
import io.studytracker.mapstruct.mapper.AssayTaskMapper;
import io.studytracker.model.Activity;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayTask;
import io.studytracker.model.User;
import io.studytracker.service.AssayTaskService;
import java.util.List;
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

  @Autowired
  private AssayTaskService assayTaskService;

  @Autowired
  private AssayTaskMapper mapper;

  @GetMapping("")
  public List<AssayTaskDto> fetchTasks(@PathVariable("assayId") String assayId) {
    Assay assay = this.getAssayFromIdentifier(assayId);
    return mapper.toDtoList(assayTaskService.findAssayTasks(assay));
  }

  @PostMapping("")
  public HttpEntity<AssayTaskDto> addTask(@PathVariable("assayId") String assayId,
      @RequestBody AssayTaskDto dto) {

    Assay assay = this.getAssayFromIdentifier(assayId);

    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);
    assay.setLastModifiedBy(user);

    AssayTask task = mapper.fromDto(dto);
    assayTaskService.addAssayTask(task, assay);

    Activity activity = AssayActivityUtils.fromTaskAdded(assay, user, task);
    this.getActivityService().create(activity);
    this.getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(mapper.toDto(task), HttpStatus.OK);

  }

  @PutMapping("")
  public HttpEntity<AssayTaskDto> updateTask(@PathVariable("assayId") String assayId,
      @RequestBody AssayTaskDto dto) {

    Assay assay = this.getAssayFromIdentifier(assayId);

    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);
    assay.setLastModifiedBy(user);

    AssayTask task = mapper.fromDto(dto);
    assayTaskService.updateAssayTask(task, assay);

    Activity activity = AssayActivityUtils.fromAssayTaskUpdate(assay, user, task);
    this.getActivityService().create(activity);
    this.getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(mapper.toDto(task), HttpStatus.OK);

  }

  @DeleteMapping("")
  public HttpEntity<?> removeTask(@PathVariable("assayId") String assayId,
      @RequestBody AssayTaskDto dto) {

    Assay assay = this.getAssayFromIdentifier(assayId);

    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);
    assay.setLastModifiedBy(user);

    AssayTask task = mapper.fromDto(dto);
    assayTaskService.deleteAssayTask(task, assay);

    Activity activity = AssayActivityUtils.fromTaskDeleted(assay, user, task);
    this.getActivityService().create(activity);
    this.getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(HttpStatus.OK);

  }

}
