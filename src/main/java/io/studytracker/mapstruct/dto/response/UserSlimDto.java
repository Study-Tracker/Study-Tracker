package io.studytracker.mapstruct.dto.response;

import lombok.Data;

@Data
public class UserSlimDto {

  private Long id;
  private String username;
  private String displayName;
  private String email;
}
