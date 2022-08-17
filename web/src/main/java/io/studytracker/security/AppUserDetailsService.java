package io.studytracker.security;

import io.studytracker.model.User;
import io.studytracker.security.AppUserDetails.AuthMethod;
import io.studytracker.service.UserService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService, SAMLUserDetailsService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AppUserDetailsService.class);

  @Autowired private UserService userService;

  @Override
  public AppUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userService.findByEmail(username).orElse(null);
    if (user != null) {
      LOGGER.info("Loaded user details for username: {}", username);
      return new AppUserDetails(user, AuthMethod.DATABASE);
    } else {
      LOGGER.warn("Could not load user details for username: {}", username);
      throw new UsernameNotFoundException(username);
    }
  }

  @Override
  public Object loadUserBySAML(SAMLCredential samlCredential) throws UsernameNotFoundException {
    String email = samlCredential.getNameID().getValue();
    Optional<User> optional = userService.findByEmail(email);
    if (optional.isPresent()) {
      User user = optional.get();
      LOGGER.info("Loading user by SAMLCredentials: {}", email);
      return new AppUserDetails(user, AuthMethod.SAML);
    } else {
      LOGGER.warn("Could not load user details for identifier: {}", email);
      throw new UsernameNotFoundException(email);
    }
  }

}
