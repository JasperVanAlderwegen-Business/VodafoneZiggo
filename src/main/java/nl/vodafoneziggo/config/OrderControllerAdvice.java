package nl.vodafoneziggo.config;

import lombok.extern.slf4j.Slf4j;
import nl.vodafoneziggo.orders.model.ErrorResponse;
import org.apache.camel.CamelExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * A global exception handler for handling various exceptions in the application.
 * This class provides centralized exception handling advice for controllers in the system.
 * It is exposed as a REST controller advice to globally catch and handle exceptions.
 * <p>
 * The following are the types of exceptions handled:
 * - CamelExecutionException: Handles execution-related errors occurring in the Camel context.
 * - ResponseStatusException: Handles HTTP status-based exceptions such as 400 or 409.
 * - MethodArgumentNotValidException: Handles validation errors for method arguments.
 * - Exception: A catch-all for handling uncategorized exceptions.
 */
@RestControllerAdvice
@Slf4j
public class OrderControllerAdvice {

    /**
     * Handles exceptions of type {@link CamelExecutionException} that occur during Camel route execution.
     * If the root cause of the exception is a {@link ResponseStatusException}, it delegates the handling
     * to the {@code handleResponseStatus} method. Otherwise, it logs the exception and returns a
     * {@link ResponseEntity} with a status of {@link HttpStatus#BAD_REQUEST}.
     *
     * @param ex the exception instance of {@link CamelExecutionException}
     * @return a {@link ResponseEntity} containing an error message derived from the exception's message
     * or its root cause's message, with a {@link HttpStatus#BAD_REQUEST} status
     */
    @ExceptionHandler(CamelExecutionException.class)
    public ResponseEntity<?> handleCamelExecution(CamelExecutionException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof ResponseStatusException rse) {
            return handleResponseStatus(rse);
        }
        log.error("Camel execution failed", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cause != null ? cause.getMessage() : ex.getMessage());
    }

    /**
     * Handles exceptions of type {@link ResponseStatusException} and constructs a standardized error response.
     * It derives the error code from the status code of the exception and uses the reason phrase as the error
     * message. If the reason is not provided, a default message is used. The method returns a {@link ResponseEntity}
     * with the appropriate HTTP status and the error response body.
     *
     * @param ex the exception instance of {@link ResponseStatusException} containing status code and reason
     * @return a {@link ResponseEntity} containing the error response with an error code and a message, and the
     * HTTP status derived from the exception's status code
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setCode(codeForStatus(ex.getStatusCode().value()));
        error.setMessage(ex.getReason() != null ? ex.getReason() : "Request failed");
        log.error("Error response from external service: {}", error, ex);
        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }

    /**
     * Handles exceptions of type {@link MethodArgumentNotValidException} that occur during
     * validation of method arguments. Constructs and returns a standardized error response
     * with details of the validation errors.
     *
     * @param ex the exception instance of {@link MethodArgumentNotValidException} that contains
     *           details about the failed validation, including field errors.
     * @return a {@link ResponseEntity} containing an {@link ErrorResponse} object with an error code,
     * a message indicating invalid request, and the list of validation error details. The HTTP
     * status is set to {@link HttpStatus#BAD_REQUEST}.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        log.error("Validation error: {}", details, ex);
        ErrorResponse error = new ErrorResponse();
        error.setCode("VALIDATION_ERROR");
        error.setMessage("Invalid request");
        error.setDetails(details);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles generic exceptions of type {@link Exception} and constructs a standardized error response.
     * Logs the exception details and returns a {@link ResponseEntity} with a standardized error payload.
     *
     * @param ex the exception instance of {@link Exception} that was thrown during execution
     * @return a {@link ResponseEntity} containing an {@link ErrorResponse} object with a generic error code,
     * a message indicating an internal error, and the exception message as part of the details.
     * The HTTP status is set to {@link HttpStatus#BAD_REQUEST}.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("An unspecified exception was thrown", ex);
        ErrorResponse error = new ErrorResponse();
        error.setCode("INTERNAL_ERROR");
        error.setMessage("An error occurred");
        error.setDetails(List.of(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    private String codeForStatus(int status) {
        return switch (status) {
            case 400 -> "VALIDATION_ERROR";
            case 404 -> "ORDER_NOT_FOUND";
            case 409 -> "DUPLICATE_ORDER";
            case 502 -> "EXTERNAL_SERVICE_ERROR";
            default -> "ERROR";
        };
    }
}
