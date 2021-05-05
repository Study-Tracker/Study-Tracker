package com.decibeltx.studytracker.security;

import com.decibeltx.studytracker.model.User;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AppUserDetails implements UserDetails {

  private final AuthMethod authMethod;

  private final User user;

  public AppUserDetails(User user, AuthMethod authMethod) {
    this.authMethod = authMethod;
    this.user = user;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return user.getAuthorities();
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getUsername();
  }

  @Override
  public boolean isAccountNonExpired() {
    return !user.isExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    return !user.isLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return !user.isCredentialsExpired();
  }

  @Override
  public boolean isEnabled() {
    return user.isActive();
  }

  public AuthMethod getAuthMethod() {
    return authMethod;
  }

  public User getUser() {
    return user;
  }

  public enum AuthMethod {
    DATABASE,
    SAML
  }

}
