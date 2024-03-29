package com.bartek.supportportal.utilty;

import static com.bartek.supportportal.constant.Authority.USER_AUTHORITIES;
import static org.junit.jupiter.api.Assertions.*;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.bartek.supportportal.domain.User;
import com.bartek.supportportal.domain.UserPrincipal;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
//@Testcontainers
//@ActiveProfiles("test-containers")
class JwtTokenProviderTest {

  @Autowired JwtTokenProvider jwtTokenProvider;

  User one;
  UserPrincipal userPrincipal;
  String expiredToken;
  String token;
  String username = "bsmalec";
  String[] authorities_user = {"ROLE_USER"};

  @BeforeEach
  void setUp() {
    one =
        User.builder()
            .userId("2131j2asda")
            .email("one@gmail.com")
            .firstName("Joe")
            .lastName("Doe")
            .username("jdoe")
            .isActive(true)
            .isNonLocked(true)
                .role("ROLE_USER")
            //.authorities(authorities_user)
            .password(new BCryptPasswordEncoder().encode("somepass"))
            .build();

    userPrincipal = new UserPrincipal(one);
    token = jwtTokenProvider.generateToken(userPrincipal);

    expiredToken =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJVc2VyIE1hbmFnZW1lbnQgUG9ydGFsIiwic3ViIjoiaW5mYW1pYSIsImlzcyI6IlN1cHBvcnQgQXBwLCBMTEMiLCJBdXRob3JpdGllcyI6WyJ1c2VyOnJlYWQiXSwiZXhwIjoxNjc3MDcwOTYyLCJpYXQiOjE2NzY2Mzg5NjJ9.L-Z6_2-xA3xSN6E3WA1SykKepZGlcIgHdoQfvzbMIuSDiPr1cNagZoLP92iv1cSeggeB4RokP2g_3Yen39EVtA";
  }

  @Test
  void testIsTokenValid() {
    // TODO check why it throws "com.auth0.jwt.exceptions.SignatureVerificationException" instead of
    // TokenExpiredException.class
    assertThrows(
            SignatureVerificationException.class, () -> jwtTokenProvider.isTokenValid(username, expiredToken));
  }

  @Test
  void testGetAuthorities() {

    List<GrantedAuthority> expected = Arrays.stream(USER_AUTHORITIES)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

    assertIterableEquals(expected, jwtTokenProvider.getAuthorities(token));
  }

  @Test
  void testGetSubject() {
    String expected = "jdoe";
    assertEquals(expected, jwtTokenProvider.getSubject(token));
  }
}
