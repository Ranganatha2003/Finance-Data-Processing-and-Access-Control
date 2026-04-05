package com.example.financebackend.service;

import com.example.financebackend.dto.users.UserResponse;
import com.example.financebackend.dto.users.UpdateUserRoleRequest;
import com.example.financebackend.dto.users.UpdateUserStatusRequest;
import com.example.financebackend.entity.User;
import com.example.financebackend.enums.Role;
import com.example.financebackend.enums.UserStatus;
import com.example.financebackend.exception.ResourceNotFoundException;
import com.example.financebackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<UserResponse> getAllUsers() {
    return userRepository.findAll().stream()
        .map(this::toUserResponse)
        .toList();
  }

  public UserResponse getUserById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

    return toUserResponse(user);
  }

  public UserResponse updateUserRole(Long userId, UpdateUserRoleRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

    Role newRole = request.role();
    user.setRole(newRole);
    userRepository.save(user);

    return toUserResponse(user);
  }

  public UserResponse updateUserStatus(Long userId, UpdateUserStatusRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

    UserStatus newStatus = request.status();
    user.setStatus(newStatus);
    userRepository.save(user);

    return toUserResponse(user);
  }

  private UserResponse toUserResponse(User user) {
    return new UserResponse(
        user.getId(),
        user.getName(),
        user.getEmail(),
        user.getRole(),
        user.getStatus()
    );
  }
}

