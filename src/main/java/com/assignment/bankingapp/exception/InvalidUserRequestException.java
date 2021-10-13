package com.assignment.bankingapp.exception;

public class InvalidUserRequestException extends RuntimeException{
    public InvalidUserRequestException(String message) {
        super(message);
    }
}
