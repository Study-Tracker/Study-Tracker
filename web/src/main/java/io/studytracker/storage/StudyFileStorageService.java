package io.studytracker.storage;

import io.studytracker.model.Assay;
import io.studytracker.model.Program;
import io.studytracker.model.Study;
import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;

public interface StudyFileStorageService {

  StorageFolder findStorageFolder(Study study) throws StudyStorageNotFoundException;

  StorageFolder createStorageFolder(Study study) throws StudyStorageException;

  StorageFolder findStorageFolder(Program program, String path) throws StudyStorageNotFoundException;

  StorageFolder createStorageFolder(Program program) throws StudyStorageException;

  StorageFolder findStorageFolder(Assay assay) throws StudyStorageNotFoundException;

  StorageFolder createStorageFolder(Assay assay) throws StudyStorageException;

}
