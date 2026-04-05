package com.example.financebackend.service;

import com.example.financebackend.dto.auth.AuthResponse;
import com.example.financebackend.dto.auth.LoginRequest;
import com.example.financebackend.dto.auth.RegisterRequest;
import com.example.financebackend.entity.User;
import com.example.financebackend.enums.Role;
import com.example.financebackend.enums.UserStatus;
import com.example.financebackend.exception.DuplicateEmailException;
import com.example.financebackend.exception.InactiveUserException;
import com.example.financebackend.repository.UserRepository;
import com.example.financebackend.security.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
  }

  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new DuplicateEmailException("Email is already registered");
    }

    User user = new User();
    user.setName(request.name());
    user.setEmail(request.email());
    user.setPassword(passwordEncoder.encode(request.password()));

    // Simplification for the assignment:
    // New users are created as VIEWER and can be upgraded by ADMIN later.
    user.setRole(Role.VIEWER);
    user.setStatus(UserStatus.ACTIVE);

    userRepository.save(user);

    String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
    return new AuthResponse(token);
  }

  public AuthResponse login(LoginRequest request) {
    User user = userRepository.findByEmail(request.email())
        .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

    if (user.getStatus() == null || user.getStatus() != UserStatus.ACTIVE) {
      throw new InactiveUserException("User account is inactive");
    }

    boolean passwordMatches = passwordEncoder.matches(request.password(), user.getPassword());
    if (!passwordMatches) {
      throw new BadCredentialsException("Invalid email or password");
    }

    String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
    return new AuthResponse(token);
  }
}

