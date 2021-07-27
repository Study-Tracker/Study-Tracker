package com.decibeltx.studytracker.mapstruct.dto;

import lombok.Data;

@Data
public class UserSlimDto {

  private Long id;
  private String username;
  private String displayName;
  private String email;

}
