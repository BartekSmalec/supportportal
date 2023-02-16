package com.bartek.supportportal.service;

import com.bartek.supportportal.domain.User;
import com.bartek.supportportal.exception.domain.EmailExistException;
import com.bartek.supportportal.exception.domain.UserNotFoundException;
import com.bartek.supportportal.exception.domain.UsernameExistException;

import java.util.List;

public interface UserService {
    User register(String firstName, String lastName, String username, String email) throws UserNotFoundException, EmailExistException, UsernameExistException;

    List<User> getUsers();

    User findUserByUsername(String username);

    User findUserByEmail(String email);
}
