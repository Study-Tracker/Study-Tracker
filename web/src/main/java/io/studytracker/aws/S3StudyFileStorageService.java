package io.studytracker.aws;

import io.studytracker.model.Assay;
import io.studytracker.model.FileStorageLocation;
import io.studytracker.model.Program;
import io.studytracker.model.Study;
import io.studytracker.repository.StudyRepository;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StorageLocationType;
import io.studytracker.storage.StoragePermissions;
import io.studytracker.storage.StudyFileStorageService;
import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class S3StudyFileStorageService implements StudyFileStorageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(S3StudyFileStorageService.class);

  @Value("${aws.s3.default-study-location}")
  private String defaultStudyLocation;

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
  public StorageFolder findStorageFolder(Study study) throws StudyStorageNotFoundException {
    return null;
  }

  @Override
  public StorageFolder createStorageFolder(Study study) throws StudyStorageException {
    LOGGER.info("Creating S3 storage folder for study: {}", study.getCode());
    String[] bits = defaultStudyLocation.replaceAll("s3://", "").split("/", 2);
    String bucketName = bits[0];
    String path = bits[1] + (bits[1].endsWith("/") ? "" : "/");
    String folderName = study.getProgram().getName() + "/" + study.getCode() + " - " + study.getName();
    FileStorageLocation location = new FileStorageLocation();
    location.setName(bucketName);
    location.setRootFolderPath("");
    location.setDisplayName(bucketName);
    location.setType(StorageLocationType.AWS_S3);
    location.setPermissions(StoragePermissions.READ_WRITE);
    StorageFolder storageFolder =  s3Service.createFolder(location, path, folderName);
    updateStudyAttributes(study, storageFolder);
    return storageFolder;
  }

  @Override
  public StorageFolder findStorageFolder(Program program, String path)
      throws StudyStorageNotFoundException {
    return null;
  }

  @Override
  public StorageFolder createStorageFolder(Program program) throws StudyStorageException {
    return null;
  }

  @Override
  public StorageFolder findStorageFolder(Assay assay) throws StudyStorageNotFoundException {
    return null;
  }

  @Override
  public StorageFolder createStorageFolder(Assay assay) throws StudyStorageException {
    return null;
  }

}
