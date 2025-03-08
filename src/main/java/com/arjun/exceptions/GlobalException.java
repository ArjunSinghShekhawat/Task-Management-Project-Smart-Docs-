package com.arjun.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> userAlreadyExistsExceptionHandler(UserAlreadyExistsException ue, WebRequest req){
        ErrorDetails err= new ErrorDetails(ue.getMessage(),req.getDescription(false), LocalDateTime.now());
        return new ResponseEntity<ErrorDetails>(err, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorDetails> userExceptionHandler(UserException ue, WebRequest req){
        ErrorDetails err= new ErrorDetails(ue.getMessage(),req.getDescription(false), LocalDateTime.now());
        return new ResponseEntity<ErrorDetails>(err, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String,String>resp = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error->{
            String fieldName = ((FieldError)error).getField();
            String defaultMessage = error.getDefaultMessage();
            resp.put(fieldName,defaultMessage);
        });

        return new ResponseEntity<>(resp,HttpStatus.BAD_REQUEST);
    }
}
