package io.studytracker.controller.api;

import io.studytracker.model.User;
import io.studytracker.security.AppUserDetails;
import io.studytracker.security.AppUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public abstract class AbstractAPIController {

  private AppUserDetailsService userDetailsService;

  /**
   * Returns the currently logged in user, or throws a {@link UsernameNotFoundException} if no user
   *   is logged in.
   *
   * @return the currently logged in user
   */
  protected User getAuthenticatedUser() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    try {
      AppUserDetails userDetails = userDetailsService.loadUserByUsername(username);
      return userDetails.getUser();
    } catch (Exception e) {
      throw new UsernameNotFoundException("User not found: " + username);
    }
  }

  public UserDetailsService getUserDetailsService() {
    return userDetailsService;
  }

  @Autowired
  public void setUserDetailsService(AppUserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }
}
