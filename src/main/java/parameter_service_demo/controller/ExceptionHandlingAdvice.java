package parameter_service_demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import parameter_service_demo.dto.ErrorDto;
import parameter_service_demo.dto.ValidationErrorDto;
import parameter_service_demo.exception.EntityNotFoundException;
import parameter_service_demo.exception.ImmutableParameterChangeException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionHandlingAdvice {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDto> handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, request.getRequestURI(), e.getMessage());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class, ImmutableParameterChangeException.class})
    public ResponseEntity<ErrorDto> handleBadRequestException(Exception e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, request.getRequestURI(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDto> handleHttpMessageNotReadableException(MethodArgumentNotValidException e, HttpServletRequest request) {
        var fieldErrors = e.getFieldErrors().stream().collect(Collectors.groupingBy(
                FieldError::getField,
                Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
        ));
        return buildValidationErrorResponse(HttpStatus.BAD_REQUEST, request.getRequestURI(), "Failed to parse request body", fieldErrors);
    }

    private ResponseEntity<ErrorDto> buildErrorResponse(HttpStatus status, String path, String message) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        ErrorDto.builder()
                                .timestamp(LocalDateTime.now())
                                .status(status.value())
                                .error(status.getReasonPhrase())
                                .path(path)
                                .message(message)
                                .build()
                );
    }

    @SuppressWarnings("SameParameterValue")
    private ResponseEntity<ValidationErrorDto> buildValidationErrorResponse(HttpStatus status, String path, String message, Map<String, List<String>> fieldErrors) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        ValidationErrorDto.builder()
                                .timestamp(LocalDateTime.now())
                                .status(status.value())
                                .error(status.getReasonPhrase())
                                .path(path)
                                .message(message)
                                .fieldErrors(fieldErrors)
                                .build()
                );
    }
}
