package com.example.financebackend.dto.users;

import com.example.financebackend.enums.Role;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleRequest(
    @NotNull(message = "Role is required")
    Role role
) {}

