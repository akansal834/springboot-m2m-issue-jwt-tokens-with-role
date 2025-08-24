package com.token.auth_api_m2m.exception;

public class InvalidRequestException extends RuntimeException{

    public InvalidRequestException(String msg){
        super(msg);
    }
}
