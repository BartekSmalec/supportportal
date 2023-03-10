package com.bartek.supportportal.repository;

import com.bartek.supportportal.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  User findUserByUsername(String username);

  User findUserByEmail(String email);

  void deleteByUsername(String username);
}
