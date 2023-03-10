package com.bartek.supportportal.listeners;

import com.bartek.supportportal.domain.UserPrincipal;
import com.bartek.supportportal.service.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationSuccessListener {
  private final LoginAttemptService loginAttemptService;

  @EventListener
  public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
    Object principal = event.getAuthentication().getPrincipal();
    if (principal instanceof UserPrincipal) {
      UserPrincipal user = (UserPrincipal) principal;
      loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
    }
  }
}
