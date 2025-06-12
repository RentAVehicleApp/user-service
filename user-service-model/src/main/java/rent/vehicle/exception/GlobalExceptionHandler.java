package rent.vehicle.exception;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import rent.vehicle.dto.response.ErrorResponse;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // Validation exception handler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            WebRequest request
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put((fieldName), errorMessage);
        });

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timeStamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Invalid input parameters")
                .details(errors)
                .path(request.getDescription(false))
                .build();
        return ResponseEntity.badRequest().body(errorResponse);

    }
    // User not found exception handler
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserPrincipalNotFoundException(
            UserNotFoundException ex,
            WebRequest request
    ){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timeStamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not found")
                .message(ex.getMessage())
                .path(request.getDescription(false))
                .build();

        log.error("User not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    //User already exists exception handler
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(
            UserAlreadyExistsException ex,
            WebRequest request
    ){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timeStamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error("User already exists")
                .message(ex.getMessage())
                .path(request.getDescription(false))
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    //Users age is unacceptable exception handler
    @ExceptionHandler(InvalidUserAgeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUserAgeException(
            InvalidUserAgeException ex,
            WebRequest request
    ){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timeStamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .path(request.getDescription(false))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    //General error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex, WebRequest request)
    {
     ErrorResponse errorResponse = ErrorResponse.builder()
             .timeStamp(Instant.now())
             .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
             .error("Internal Server Error")
             .message("An unexpected occurred")
             .path(request.getDescription(false))
             .build();
     log.error("Internal Server Error: {}", ex);
     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
