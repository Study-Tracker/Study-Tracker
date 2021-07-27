package com.decibeltx.studytracker.mapstruct.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDetailsDto {

  private Long id;
  private @NotNull String username;
  private String department;
  private String title;
  private @NotNull String displayName;
  private @NotNull String email;
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
