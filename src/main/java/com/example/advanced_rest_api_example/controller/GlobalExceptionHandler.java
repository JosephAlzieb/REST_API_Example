package com.example.advanced_rest_api_example.controller;

import com.example.advanced_rest_api_example.exception.ApiError;
import com.example.advanced_rest_api_example.exception.ResourceNotFoundException;
import com.example.advanced_rest_api_example.logging.HasLogger;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler implements HasLogger {

    /**
     * Validierungsfehler abfangen (z.B. @Valid)
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    @ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)))
    })
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request){

        List<String> validationErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.toList());

        ApiError apiError = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "Validation failed",
            request.getDescription(false).replace("uri=", ""),
            validationErrors);

        getLogger().warn("Validation error: {}", validationErrors);

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    /**
     * Eigene NotFound Exception
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Resource not found",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)))
    })
    protected ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
        ApiError apiError = new ApiError(
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        getLogger().warn("Resource not found: {}", ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    /**
     * Alle anderen Exceptions (500 Internal Server Error)
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "Internal Server Error",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)))
    })
    protected ResponseEntity<ApiError> handleAllExceptions(Exception ex, WebRequest request) {
        ApiError apiError = new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            "Internal Server Error",
            request.getDescription(false).replace("uri=", "")
        );
        logger.error("Unhandled exception", ex);
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
