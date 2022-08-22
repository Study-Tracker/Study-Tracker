package io.studytracker.mapstruct.dto.elasticsearch;

import io.studytracker.model.Status;
import io.studytracker.search.AssaySearchDocument;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "st-assays")
@Data
public class ElasticsearchAssayDocument implements AssaySearchDocument<Long> {

  @Id private Long id;

  private String code;

  private String name;

  private String description;

  @Field(type = FieldType.Nested, includeInParent = true)
  private ElasticsearchAssayTypeDocument assayType;

  private Status status;

  @Field(type = FieldType.Nested, includeInParent = true)
  private ElasticsearchStudySummaryDocument study;

  @Field(type = FieldType.Nested, includeInParent = true)
  private ElasticsearchUserDocument createdBy;

  @Field(type = FieldType.Nested, includeInParent = true)
  private ElasticsearchUserDocument lastModifiedBy;

  private Date startDate;

  private Date endDate;

  private Date createdAt;

  private Date updatedAt;

  @Field(type = FieldType.Nested, includeInParent = true)
  private ElasticsearchUserDocument owner;

  @Field(type = FieldType.Nested, includeInParent = true)
  private Set<ElasticsearchUserDocument> users = new HashSet<>();

  private Map<String, String> attributes = new HashMap<>();

  private boolean active;

  private Map<String, Object> fields = new HashMap<>();

  @Field(type = FieldType.Nested, includeInParent = true)
  private Set<ElasticsearchAssayTaskDocument> tasks;

}
