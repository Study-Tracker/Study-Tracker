package io.studytracker.mapstruct.dto.response;

import java.util.Date;
import lombok.Data;

@Data
public class StudyConclusionsDetailsDto {
  private Long id;
  private String content;
  private UserSlimDto createdBy;
  private UserSlimDto lastModifiedBy;
  private Date createdAt;
  private Date updatedAt;
}
