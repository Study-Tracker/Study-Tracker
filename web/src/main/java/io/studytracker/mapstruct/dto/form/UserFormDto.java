package io.studytracker.mapstruct.dto.form;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserFormDto {

  private Long id;
  private @NotBlank String displayName;
  private @NotBlank String email;
  private String department;
  private String title;
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
