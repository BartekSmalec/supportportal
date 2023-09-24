package com.bartek.supportportal.utilty;

import static com.bartek.supportportal.constant.SecurityConstant.*;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.bartek.supportportal.domain.UserPrincipal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {
  @Value("${jwt.secret}")
  private String secret;

  public String generateToken(UserPrincipal userPrincipal) {
    String[] claims = getClaimsFromUser(userPrincipal);
    return JWT.create()
        .withIssuer(SUPPORT_APP_LLC)
        .withAudience(SUPPORT_APP_ADMINISTRATION)
        .withIssuedAt(new Date())
        .withSubject(userPrincipal.getUsername())
        .withArrayClaim(AUTHORITIES, claims)
        .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .sign(Algorithm.HMAC512(secret));
  }

  private String[] getClaimsFromUser(UserPrincipal user) {
    List<String> authorities =
        user.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
    return authorities.toArray(new String[0]);
  }

  public List<GrantedAuthority> getAuthorities(String token) {
    String[] claims = getClaimsFromToken(token);
    return Arrays.stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
  }

  private String[] getClaimsFromToken(String token) {
    JWTVerifier verifier = getJWTVerifier();
    return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
  }

  private JWTVerifier getJWTVerifier() {
    Algorithm algorithm = Algorithm.HMAC512(secret);
    JWTVerifier verifier = JWT.require(algorithm).withIssuer(SUPPORT_APP_LLC).build();
    return verifier;
  }

  public Authentication getAuthentication(
      String username, List<GrantedAuthority> authorities, HttpServletRequest request) {
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
        new UsernamePasswordAuthenticationToken(username, null, authorities);
    usernamePasswordAuthenticationToken.setDetails(
        new WebAuthenticationDetailsSource().buildDetails(request));
    return usernamePasswordAuthenticationToken;
  }

  public boolean isTokenValid(String username, String token) {
    JWTVerifier verifier = getJWTVerifier();
    return StringUtils.isNotEmpty(username) && !isTokenExpired(verifier, token) && token != "null";
  }

  private boolean isTokenExpired(JWTVerifier verifier, String token) {
    Date expiration = verifier.verify(token).getExpiresAt();
    return expiration.before(new Date());
  }

  public String getSubject(String token) {
    JWTVerifier verifier = getJWTVerifier();
    return verifier.verify(token).getSubject();
  }
}
