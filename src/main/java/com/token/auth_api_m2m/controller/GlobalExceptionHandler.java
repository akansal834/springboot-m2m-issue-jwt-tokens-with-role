package com.token.auth_api_m2m.controller;

import com.token.auth_api_m2m.exception.InvalidCredentialsException;
import com.token.auth_api_m2m.exception.InvalidRequestException;
import com.token.auth_api_m2m.exception.KeyReadingException;
import com.token.auth_api_m2m.exception.TokenIssueException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentials(String msg){
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);

    }
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<String> handleInvalidRequest(String msg){
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler({KeyReadingException.class, TokenIssueException.class})
    public ResponseEntity<String> handleInvalidKeys(String msg){
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
