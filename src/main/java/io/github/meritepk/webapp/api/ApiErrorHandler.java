package io.github.meritepk.webapp.api;

import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiErrorHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ResponseEntityExceptionHandler delegate = new ResponseEntityExceptionHandler() {
    };

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Map<String, Object>> handleException(Exception ex, WebRequest request) {
        ResponseEntity<Map<String, Object>> result = null;
        if (ex instanceof AccessDeniedException) {
            result = handleError(HttpStatus.FORBIDDEN, "Access Denied", ex, request);
        } else if (ex instanceof DataIntegrityViolationException) {
            result = handleError(HttpStatus.CONFLICT, "Data Integrity Violation", ex, request);
        } else if (ex instanceof MethodArgumentTypeMismatchException || ex instanceof HttpMessageNotReadableException
                || ex instanceof MethodArgumentNotValidException) {
            result = handleError(HttpStatus.BAD_REQUEST, "Invalid Request", ex, request);
        }
        if (result == null) {
            try {
                ResponseEntity<Object> response = delegate.handleException(ex, request);
                if (response != null) {
                    HttpStatus status = response.getStatusCode();
                    result = handleError(status, status.getReasonPhrase(), ex, request);
                }
            } catch (Exception e) {
                result = null;
            }
            if (result == null) {
                String message = ex.getMessage();
                if (message == null) {
                    message = ex.getClass().getName();
                }
                result = handleError(HttpStatus.INTERNAL_SERVER_ERROR, message, ex, request);
            }
        }
        return result;
    }

    private ResponseEntity<Map<String, Object>> handleError(HttpStatus status, String message, Exception ex,
            WebRequest webRequest) {
        String path = webRequest.getContextPath();
        if (webRequest instanceof ServletRequestAttributes) {
            ServletRequestAttributes servletRequest = (ServletRequestAttributes) webRequest;
            path = servletRequest.getRequest().getRequestURI();
        }
        Map<String, Object> body = Map.of("status", status.value(), "error", message, "path", path,
                "timestamp", LocalDateTime.now());
        logger.warn("handleError: {}, {}", body, ex);
        return ResponseEntity.status(status).body(body);
    }
}
