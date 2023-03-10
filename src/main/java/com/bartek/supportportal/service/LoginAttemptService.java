package com.bartek.supportportal.service;

import static java.util.concurrent.TimeUnit.MINUTES;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.ExecutionException;
import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {
  private static final int MAXIMUM_NUMBER_OF_ATTEMPT = 5;
  public static final int ATTEMPT_INCREMENT = 1;
  private LoadingCache<String, Integer> loginAttemptCache;

  public LoginAttemptService() {
    super();
    loginAttemptCache =
        CacheBuilder.newBuilder()
            .expireAfterWrite(15, MINUTES)
            .maximumSize(100)
            .build(
                new CacheLoader<>() {
                  public Integer load(String key) {
                    return 0;
                  }
                });
  }

  // remove user with "key" username from cache
  public void evictUserFromLoginAttemptCache(String username) {
    loginAttemptCache.invalidate(username);
  }

  public void addUserLoginAttemptCache(String username) {
    try {
      loginAttemptCache.put(username, ATTEMPT_INCREMENT + loginAttemptCache.get(username));
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
  }

  public boolean hasExceededMaxAttempt(String username) {
    try {
      return loginAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPT;
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    return false;
  }
}
