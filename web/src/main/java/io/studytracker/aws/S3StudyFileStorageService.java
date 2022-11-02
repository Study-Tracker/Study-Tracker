package io.studytracker.aws;

import io.studytracker.model.Assay;
import io.studytracker.model.FileStorageLocation;
import io.studytracker.model.Program;
import io.studytracker.model.Study;
import io.studytracker.repository.StudyRepository;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StudyStorageService;
import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class S3StudyFileStorageService implements StudyStorageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(S3StudyFileStorageService.class);

  @Autowired
  private S3DataFileStorageService s3Service;

  @Autowired
  private StudyRepository studyRepository;

  private void updateStudyAttributes(Study study, StorageFolder folder) {
    study.setAttribute(AWSAttributes.S3_BUCKET, folder.getName());
    study.setAttribute(AWSAttributes.S3_KEY, folder.getPath());
    studyRepository.save(study);
  }

  @Override
  public StorageFolder findFolder(FileStorageLocation location, Study study) throws StudyStorageNotFoundException {
    return null;
  }

  @Override
  public StorageFolder createFolder(FileStorageLocation location, Study study) throws StudyStorageException {
    LOGGER.info("Creating S3 storage folder for study: {}", study.getCode());
    String path = location.getRootFolderPath();
    String folderName = study.getProgram().getName() + "/" + study.getCode() + " - " + study.getName();
    StorageFolder storageFolder =  s3Service.createFolder(location, path, folderName);
    updateStudyAttributes(study, storageFolder);
    return storageFolder;
  }

  @Override
  public StorageFolder findFolder(FileStorageLocation location, Program program) throws StudyStorageNotFoundException {
    return null;
  }

  @Override
  public StorageFolder createFolder(FileStorageLocation location, Program program) throws StudyStorageException {
    return null;
  }

  @Override
  public StorageFolder findFolder(FileStorageLocation location, Assay assay) throws StudyStorageNotFoundException {
    return null;
  }

  @Override
  public StorageFolder createFolder(FileStorageLocation location, Assay assay) throws StudyStorageException {
    return null;
  }

  @Override
  public StorageFile saveFile(FileStorageLocation location, File file, Study study) throws StudyStorageException {
    return null;
  }

  @Override
  public StorageFile saveFile(FileStorageLocation location, File file, Assay assay) throws StudyStorageException {
    return null;
  }

}
