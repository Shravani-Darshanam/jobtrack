package com.jobtrack.dto;
import jakarta.validation.constraints.*;
public record AuthRequest(@NotBlank @Email @Size(max = 150) String email, @NotBlank @Size(min = 6, max = 72) String password) {}
