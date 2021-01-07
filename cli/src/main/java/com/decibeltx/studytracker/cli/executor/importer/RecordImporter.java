package com.decibeltx.studytracker.cli.executor.importer;

import com.decibeltx.studytracker.cli.exception.RecordImportException;
import com.decibeltx.studytracker.core.exception.InvalidConstraintException;
import java.util.Collection;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;

public abstract class RecordImporter<T extends Persistable<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RecordImporter.class);

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  private final Class<T> model;

  public RecordImporter(Class<T> model) {
    this.model = model;
  }

  abstract void importRecord(T record) throws Exception;

  public void importRecords(Collection<T> records) throws Exception {
    boolean isError = false;
    for (T record : records) {
      try {
        importRecord(record);
      } catch (Exception e) {
        if (e instanceof InvalidConstraintException) {
          isError = true;
        } else {
          throw e;
        }
      }
    }
    if (isError) {
      throw new RecordImportException(
          String.format("Failed to import %s records", model.getName()));
    }
  }

  void validate(T record) throws InvalidConstraintException {
    Set<ConstraintViolation<T>> violations = validator.validate(record);
    if (!violations.isEmpty()) {
      String message = String.format("Constraint violations for %s record: %s",
          model.getName(), record.toString());
      LOGGER.warn(message);
      for (ConstraintViolation<T> violation : violations) {
        LOGGER.warn(violation.getPropertyPath().toString() + " " + violation.getMessage());
      }
      throw new InvalidConstraintException(message);
    }
  }

  Class<T> getModel() {
    return model;
  }

}
