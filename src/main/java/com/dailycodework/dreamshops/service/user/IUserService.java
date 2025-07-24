package com.dailycodework.dreamshops.service.user;

import com.dailycodework.dreamshops.dto.UserDto;
import com.dailycodework.dreamshops.model.User;
import com.dailycodework.dreamshops.request.CreateUserRequest;
import com.dailycodework.dreamshops.request.UpdateUserRequest;

public interface IUserService {
    User getUserById(Long userId);

    User createUser(CreateUserRequest user);

    User updateUser(UpdateUserRequest user, Long userId);

    void deleteUser(Long userId);

    // Utility DTO conversion methods
    UserDto toUserDto(User user);
}
