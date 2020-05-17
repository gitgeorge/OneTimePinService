package com.otp.exceptions;

public class ApiRequestException extends RuntimeException{
    public ApiRequestException(String s) {
        super(s);
    }
}
