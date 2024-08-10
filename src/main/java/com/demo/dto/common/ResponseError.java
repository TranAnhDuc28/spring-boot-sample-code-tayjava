package com.demo.dto.common;

public class ResponseError extends ResponseData{

    public ResponseError(int status, String message) {
        super(status, message);
    }
}
