/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

  @Value("${application.secret}")
  private String tokenSecret;

  @Value("${jwt.expiration:1440}")
  private String tokenExpiration;

  public ApiAuthorizationToken generateToken(String username) {
    Date now = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.MINUTE, Integer.parseInt(tokenExpiration));
    Date expiresAt = calendar.getTime();

    String token = JWT.create()
        .withIssuer(ISSUER)
        .withSubject(SUBJECT)
        .withClaim("username", username)
        .withIssuedAt(now)
        .withExpiresAt(expiresAt)
        .sign(Algorithm.HMAC256(tokenSecret));
    ApiAuthorizationToken authToken = new ApiAuthorizationToken();
    authToken.setToken(token);
    authToken.setCreatedAt(now.getTime());
    authToken.setExpiresAt(expiresAt.getTime());
    return authToken;
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
