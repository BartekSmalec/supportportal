package com.bartek.supportportal.domain.mappers;

import com.bartek.supportportal.domain.DTO.UserLogin;
import com.bartek.supportportal.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserLoginMapper {
  UserLoginMapper INSTANCE = Mappers.getMapper(UserLoginMapper.class);

  UserLogin userToUserLogin(User user);

  User userLoginToUser(UserLogin userLogin);
}
