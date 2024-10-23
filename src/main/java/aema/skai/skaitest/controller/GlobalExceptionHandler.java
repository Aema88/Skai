package aema.skai.skaitest.controller;

import aema.skai.skaitest.exception.InvalidInputException;
import aema.skai.skaitest.exception.InvalidPolynomialException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidPolynomialException.class)
    public ResponseEntity<String> handleInvalidPolynomialException(InvalidPolynomialException ex) {
        return ResponseEntity
                .badRequest()
                .body("Error: " + ex.getMessage());
    }
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<String> handleInvalidInputException(InvalidInputException ex) {
        return ResponseEntity
                .badRequest()
                .body("Error: " + ex.getMessage());
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity
                .badRequest()
                .body("Error: Invalid value for 'x'. Expected a number.");
    }
}