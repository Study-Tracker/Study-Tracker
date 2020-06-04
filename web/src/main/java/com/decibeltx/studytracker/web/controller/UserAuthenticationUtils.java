package com.decibeltx.studytracker.web.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public class UserAuthenticationUtils {

  public static String getUsernameFromAuthentication(Authentication authentication) {
    String accountName;
    if (authentication instanceof UsernamePasswordAuthenticationToken) {
      accountName = authentication.getName();
    } else {
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      accountName = userDetails.getUsername();
    }
    return accountName;
  }

}
