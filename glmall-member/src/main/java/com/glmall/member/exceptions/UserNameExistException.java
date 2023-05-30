package com.glmall.member.exceptions;

public class UserNameExistException extends RuntimeException{
    public UserNameExistException() {
        super("用户名已存在");
    }
}
