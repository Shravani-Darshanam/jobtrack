package com.jobtrack.exception;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<Map<String, Object>> body(HttpStatus s, String msg, Map<String, String> fields) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("status", s.value()); m.put("message", msg);
        if (fields != null) m.put("errors", fields);
        return ResponseEntity.status(s).body(m);
    }
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<Map<String, Object>> status(ResponseStatusException e) {
        return body(HttpStatus.valueOf(e.getStatusCode().value()), e.getReason(), null);
    }
    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<Map<String, Object>> notFound(NotFoundException e) { return body(HttpStatus.NOT_FOUND, e.getMessage(), null); }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> invalid(MethodArgumentNotValidException e) {
        Map<String, String> f = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(x -> f.put(x.getField(), x.getDefaultMessage()));
        return body(HttpStatus.BAD_REQUEST, "Validation failed", f);
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<Map<String, Object>> badInput(Exception e) {
        return body(HttpStatus.BAD_REQUEST, "Malformed JSON or invalid value (check enums, date format yyyy-MM-dd, id)", null);
    }
}
