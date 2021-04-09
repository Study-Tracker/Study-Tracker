package com.decibeltx.studytracker.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public class UserAuthenticationUtils {

  public static String getUsernameFromAuthentication(Authentication authentication) {
    String username;
    if (authentication instanceof UsernamePasswordAuthenticationToken) {
      username = authentication.getName();
    } else {
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      username = userDetails.getUsername();
    }
    return username;
  }

}
