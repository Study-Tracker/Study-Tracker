package io.studytracker.service;

import io.studytracker.model.Study;
import io.studytracker.model.StudyCollection;
import io.studytracker.model.User;
import io.studytracker.repository.StudyCollectionRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudyCollectionService {

  @Autowired private StudyCollectionRepository studyCollectionRepository;

  public Optional<StudyCollection> findById(Long id) {
    return studyCollectionRepository.findById(id);
  }

  public List<StudyCollection> findAll() {
    return studyCollectionRepository.findAll();
  }

  public List<StudyCollection> findByUser(User user) {
    return studyCollectionRepository.findByCreatedById(user.getId());
  }

  public List<StudyCollection> findByStudy(Study study) {
    return studyCollectionRepository.findByStudiesId(study.getId());
  }

  public StudyCollection create(StudyCollection collection) {
    return studyCollectionRepository.save(collection);
  }

  public void update(StudyCollection collection) {
    StudyCollection c = studyCollectionRepository.getById(collection.getId());
    c.setDescription(collection.getDescription());
    c.setName(collection.getName());
    c.setStudies(collection.getStudies());
    c.setShared(collection.isShared());
    studyCollectionRepository.save(c);
  }

  public void delete(StudyCollection collection) {
    studyCollectionRepository.deleteById(collection.getId());
  }

  public boolean collectionWithNameExists(StudyCollection collection, User user) {
    return studyCollectionRepository.findByCreatedById(user.getId()).stream()
        .anyMatch(
            c ->
                c.getName().equalsIgnoreCase(collection.getName())
                    && !c.getId().equals(collection.getId()));
  }
}
