package com.example.advanced_rest_api_example.security.model;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(@NotBlank String username, @NotBlank String password) {}
