package io.studytracker.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class TokenFilter extends OncePerRequestFilter {

  @Autowired
  private AppUserDetailsService userDetailsService;

  @Autowired
  private TokenUtils tokenUtils;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      if (token.isBlank()) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST,
            "Invalid token in Authorization header.");
      } else {
        try {
          String username = tokenUtils.validateToken(token);
          AppUserDetails userDetails = userDetailsService.loadUserByUsername(username);
          UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(userDetails.getUsername(),
                  userDetails.getPassword(), userDetails.getAuthorities());
          if (SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContextHolder.getContext().setAuthentication(authToken);
          }
        } catch (JWTVerificationException e) {
          response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid token.");
        }
      }
    }
    filterChain.doFilter(request, response);
  }

}
