package E_commerce.com.SecureEcommerceApplication.exception;

import E_commerce.com.SecureEcommerceApplication.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 404 Not Found ──────────────────────────────────────────
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {

        log.warn("Resource not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // ── 409 Conflict ───────────────────────────────────────────
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateResource(
            DuplicateResourceException ex, HttpServletRequest request) {

        log.warn("Duplicate resource: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    // ── 400 Business Error ─────────────────────────────────────
    @ExceptionHandler({BusinessException.class, OutOfStockException.class})
    public ResponseEntity<ApiResponse<Void>> handleBusiness(
            RuntimeException ex, HttpServletRequest request) {

        log.warn("Business error: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    // ── 401 Invalid Token ──────────────────────────────────────
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidToken(
            InvalidTokenException ex, HttpServletRequest request) {

        log.warn("Invalid token: {}", ex.getMessage());
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    // ── 401 Bad Credentials ────────────────────────────────────
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {

        log.warn("Bad credentials at: {}", request.getRequestURI());
        return build(HttpStatus.UNAUTHORIZED, "Invalid email or password", request);
    }

    // ── 401 Disabled Account ───────────────────────────────────
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse<Void>> handleDisabled(
            DisabledException ex, HttpServletRequest request) {

        log.warn("Disabled account at: {}", request.getRequestURI());
        return build(HttpStatus.UNAUTHORIZED, "Account is disabled", request);
    }

    // ── 403 Forbidden ──────────────────────────────────────────
    @ExceptionHandler({UnauthorizedException.class, AccessDeniedException.class})
    public ResponseEntity<ApiResponse<Void>> handleForbidden(
            RuntimeException ex, HttpServletRequest request) {

        log.warn("Access denied at: {}", request.getRequestURI());
        return build(
                HttpStatus.FORBIDDEN,
                "You don't have permission to perform this action",
                request
        );
    }

    // ── 400 Validation Error ───────────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fe -> fe.getField(),
                        fe -> fe.getDefaultMessage(),
                        (first, second) -> first
                ));

        log.warn("Validation error at {}: {}", request.getRequestURI(), fieldErrors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        HttpStatus.BAD_REQUEST,
                        "Validation failed",
                        request.getRequestURI(),
                        fieldErrors
                ));
    }

    // ── 400 Malformed JSON ─────────────────────────────────────
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidJson(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        log.warn("Malformed JSON at {}: {}", request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Malformed JSON request", request);
    }

    // ── 500 Fallback ───────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(
            Exception ex, HttpServletRequest request) {

        log.error("Unexpected error at {}: {}",
                request.getRequestURI(), ex.getMessage(), ex);
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                request
        );
    }

    // ── DRY helper ─────────────────────────────────────────────
    private ResponseEntity<ApiResponse<Void>> build(
            HttpStatus status, String message, HttpServletRequest request) {

        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(
                        status,
                        message,
                        request.getRequestURI()
                ));
    }
}