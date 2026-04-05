package com.example.financebackend.dto.users;

import com.example.financebackend.enums.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(
    @NotNull(message = "Status is required")
    UserStatus status
) {}

