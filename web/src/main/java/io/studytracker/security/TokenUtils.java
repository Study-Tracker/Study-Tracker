package io.studytracker.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Calendar;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;

public class TokenUtils {

  private static final String ISSUER = "Study Tracker";
  private static final String SUBJECT = "User Details";

  @Value("${jwt.secret}")
  private String tokenSecret;

  @Value("${jwt.expiration:1440}")
  private String tokenExpiration;

  public String generateToken(String username) {
    Date now = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.MINUTE, Integer.parseInt(tokenExpiration));
    Date expiresAt = calendar.getTime();

    return JWT.create()
        .withIssuer(ISSUER)
        .withSubject(SUBJECT)
        .withClaim("username", username)
        .withIssuedAt(now)
        .withExpiresAt(expiresAt)
        .sign(Algorithm.HMAC256(tokenSecret));
  }

  public String validateToken(String token) throws JWTVerificationException {
    JWTVerifier verifier = JWT.require(Algorithm.HMAC256(tokenSecret))
        .withSubject(SUBJECT)
        .withIssuer(ISSUER)
        .build();
    DecodedJWT jwt = verifier.verify(token);
    return jwt.getClaim("username").asString();
  }

}
