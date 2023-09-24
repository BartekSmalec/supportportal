package com.bartek.supportportal.service;

import com.bartek.supportportal.domain.User;
import com.bartek.supportportal.exception.domain.*;

import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
  User register(String firstName, String lastName, String username, String email, String password, String repeatPassword)
          throws UserNotFoundException, EmailExistException, UsernameExistException, PasswordsDontMatchException;

  List<User> getUsers();

  User findUserByUsername(String username);

  User findUserByEmail(String email);

  User addNewUser(
      String firstName,
      String lastName,
      String username,
      String email,
      String password,
      String role,
      boolean isNonLocked,
      boolean isActive,
      MultipartFile profileImage)
      throws UserNotFoundException, EmailExistException, UsernameExistException, IOException;

  User updateUser(
      String userName,
      String newFirstName,
      String newLastName,
      String newUsername,
      String newEmail,
      String role,
      boolean isNonLocked,
      boolean isActive,
      MultipartFile profileImage)
      throws UserNotFoundException, EmailExistException, UsernameExistException, IOException;

  void deleteUser(String username) throws IOException;

  void resetPassword(String email) throws EmailNotFoundException;

  User updateProfileImage(String username, MultipartFile profileImage)
      throws UserNotFoundException, EmailExistException, UsernameExistException, IOException;
}
