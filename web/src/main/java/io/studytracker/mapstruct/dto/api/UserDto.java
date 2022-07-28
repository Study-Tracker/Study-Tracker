package io.studytracker.mapstruct.dto.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class UserDto {

  private Long id;
  private String department;
  private String title;
  private String displayName;
  private String email;
  private boolean admin = false;
  private Date createdAt;
  private Date updatedAt;
  private Map<String, String> attributes = new HashMap<>();
  private boolean active = true;
  private boolean locked = false;
  private boolean expired = false;
  private boolean credentialsExpired = false;
  private Map<String, String> configuration = new HashMap<>();
}
