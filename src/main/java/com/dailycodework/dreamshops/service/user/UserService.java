package com.dailycodework.dreamshops.service.user;

import java.util.Collections;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dailycodework.dreamshops.dto.UserDto;
import com.dailycodework.dreamshops.exceptions.AlreadyExistsException;
import com.dailycodework.dreamshops.exceptions.ResourceNotFoundException;
import com.dailycodework.dreamshops.model.Role;
import com.dailycodework.dreamshops.model.User;
import com.dailycodework.dreamshops.repository.RoleRepository;
import com.dailycodework.dreamshops.repository.UserRepository;
import com.dailycodework.dreamshops.request.CreateUserRequest;
import com.dailycodework.dreamshops.request.UpdateUserRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found!"));
    }

    @Override
    public User createUser(CreateUserRequest user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new AlreadyExistsException(
                    "User with this email already exists!");
        }

        User newUser = new User();
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));

        // Assign default USER role
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            // Create default role if it doesn't exist
            userRole = new Role("ROLE_USER");
            roleRepository.save(userRole);
        }
        newUser.setRoles(Collections.singletonList(userRole));

        return userRepository.save(newUser);
    }

    @Override
    public User updateUser(UpdateUserRequest user, Long userId) {
        User existingUser = getUserById(userId);
        if (user.getFirstName() == null && user.getLastName() == null) {
            throw new IllegalArgumentException(
                    "At least one field must be provided for update.");
        }
        if (user.getFirstName() != null)
            existingUser.setFirstName(user.getFirstName());
        if (user.getLastName() != null)
            existingUser.setLastName(user.getLastName());
        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // Utility DTO conversion methods
    public UserDto toUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }
}
