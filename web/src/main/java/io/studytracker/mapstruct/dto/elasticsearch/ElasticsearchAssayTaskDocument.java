package io.studytracker.mapstruct.dto.elasticsearch;

import io.studytracker.model.TaskStatus;
import java.util.Date;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
public class ElasticsearchAssayTaskDocument {

  private Long id;

  private TaskStatus status;

  private String label;

  private Integer order;

  private Date createdAt;

  private Date updatedAt;

  @Field(type = FieldType.Nested, includeInParent = true)
  private ElasticsearchUserDocument createdBy;

  @Field(type = FieldType.Nested, includeInParent = true)
  private ElasticsearchUserDocument lastModifiedBy;

  @Field(type = FieldType.Nested, includeInParent = true)
  private ElasticsearchUserDocument assignedTo;

  private Date dueDate;

}
