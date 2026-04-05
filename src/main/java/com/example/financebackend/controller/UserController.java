package com.example.financebackend.controller;

import com.example.financebackend.dto.users.UpdateUserRoleRequest;
import com.example.financebackend.dto.users.UpdateUserStatusRequest;
import com.example.financebackend.dto.users.UserResponse;
import com.example.financebackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<List<UserResponse>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  @PutMapping("/{id}/role")
  public ResponseEntity<UserResponse> updateUserRole(
      @PathVariable Long id,
      @Valid @RequestBody UpdateUserRoleRequest request
  ) {
    return ResponseEntity.ok(userService.updateUserRole(id, request));
  }

  @PutMapping("/{id}/status")
  public ResponseEntity<UserResponse> updateUserStatus(
      @PathVariable Long id,
      @Valid @RequestBody UpdateUserStatusRequest request
  ) {
    return ResponseEntity.ok(userService.updateUserStatus(id, request));
  }
}

