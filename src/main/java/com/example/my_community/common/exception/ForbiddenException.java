package com.example.my_community.common.exception;

public class ForbiddenException extends RuntimeException{
    public ForbiddenException() {
        super("권한이 없습니다.");
    }

    public ForbiddenException(String message) {
        super(message);
    }
}
