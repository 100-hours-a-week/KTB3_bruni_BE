package com.example.my_community.common.exception;

public class NotFoundException extends RuntimeException{
    public NotFoundException() { super("리소스를 찾을 수 없습니다."); }
    public NotFoundException(String message) { super(message); }
}
