package com.example.advanced_rest_api_example.security.model;

public record TokenResponse(String accessToken, String refreshToken, String tokenType) {}
