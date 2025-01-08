package br.devluan.easystock.controllers.handler;

import br.devluan.easystock.domain.exceptions.UserExceptions.EmailAlreadyExistsException;
import br.devluan.easystock.domain.exceptions.ProductExceptions.ProductValidationException;
import br.devluan.easystock.dto.response.ErrorResponse;
import br.devluan.easystock.domain.exceptions.UserExceptions.UserErrorExceptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserErrorExceptions.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserErrorExceptions ex) {
        ErrorResponse response = new ErrorResponse(
                "User not found",
                ex.getMessage(),
                List.of("User with the provided details was not found.")
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistsException ex) {
        ErrorResponse response = new ErrorResponse(
                "Validation error",
                ex.getMessage(),
                List.of("The provided email is already in use.")
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ProductValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ProductValidationException ex) {
        ErrorResponse response = new ErrorResponse(
                "Validation error",
                ex.getMessage(),
                ex.getErrors()
        );
        return ResponseEntity.badRequest().body(response);
    }
}
