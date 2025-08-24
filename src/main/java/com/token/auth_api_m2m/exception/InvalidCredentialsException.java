package com.token.auth_api_m2m.exception;

public class InvalidCredentialsException extends RuntimeException{

    public InvalidCredentialsException(String msg){
        super(msg);
    }
}
