package com.example.advanced_rest_api_example.security.controller;

import com.example.advanced_rest_api_example.controller.GlobalExceptionHandler;
import com.example.advanced_rest_api_example.exception.ApiError;
import com.example.advanced_rest_api_example.exception.ResourceNotFoundException;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class SecurityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(SecurityExceptionHandler.class);

    private ApiError buildApiError(HttpStatus status, String message, WebRequest request, List<String> errors) {
        return new ApiError(
            status.value(),
            status.getReasonPhrase(),
            message,
            request.getDescription(false).replace("uri=", ""),
            errors
        );
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Resource not found",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)))
    })
    protected ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        ApiError apiError = buildApiError(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)))
    })
    protected ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex, WebRequest request) {
        log.warn("Bad credentials: {}", ex.getMessage());
        ApiError apiError = buildApiError(HttpStatus.UNAUTHORIZED, "Invalid username or password", request, null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

    @ExceptionHandler(JwtException.class)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "JWT error",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)))
    })
    protected ResponseEntity<ApiError> handleJwt(JwtException ex, WebRequest request) {
        log.warn("JWT exception: {}", ex.getMessage());
        ApiError apiError = buildApiError(HttpStatus.UNAUTHORIZED, "Invalid or expired token", request, null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)))
    })
    protected ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> validationErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(f -> f.getField() + ": " + f.getDefaultMessage())
            .toList();

        log.warn("Validation failed: {}", validationErrors);
        ApiError apiError = buildApiError(HttpStatus.BAD_REQUEST, "Validation failed", request, validationErrors);
        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(Exception.class)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "Unexpected error",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)))
    })
    protected ResponseEntity<ApiError> handleGeneral(Exception ex, WebRequest request) {
        log.error("Unexpected error", ex);
        ApiError apiError = buildApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request, null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }
}
