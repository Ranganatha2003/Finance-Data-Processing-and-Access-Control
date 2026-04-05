package com.example.financebackend.dto.users;

import com.example.financebackend.enums.Role;
import com.example.financebackend.enums.UserStatus;

public record UserResponse(
    Long id,
    String name,
    String email,
    Role role,
    UserStatus status
) {}

