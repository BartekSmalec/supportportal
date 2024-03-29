package com.bartek.supportportal.filter;

import static com.bartek.supportportal.constant.SecurityConstant.OPTIONS_HTTP_METHOD;
import static com.bartek.supportportal.constant.SecurityConstant.TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;

import com.bartek.supportportal.utilty.JwtTokenProvider;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@AllArgsConstructor
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)) {
      response.setStatus(OK.value());
    } else {
      String authorizationHeader = request.getHeader(AUTHORIZATION);
      if (StringUtils.isEmpty(authorizationHeader)
          || !StringUtils.startsWith(authorizationHeader, TOKEN_PREFIX)) {
        filterChain.doFilter(request, response);
        return;
      }
      String token = authorizationHeader.substring(TOKEN_PREFIX.length());
      String username = jwtTokenProvider.getSubject(token);
      if (jwtTokenProvider.isTokenValid(username, token)) {
        List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);
        Authentication authentication =
            jwtTokenProvider.getAuthentication(username, authorities, request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } else {
        SecurityContextHolder.clearContext();
      }
    }
    filterChain.doFilter(request, response);
  }
}
