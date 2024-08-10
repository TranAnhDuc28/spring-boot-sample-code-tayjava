package com.demo.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @param <T> kiểu generic tự động in ra các mô tả ở Swagger UI thay vì phải viết thủ công
 */

public class ResponseData<T> {
    private final int status;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL) // khi data null thì sẽ không hiển thị
    private T data;

    // sử dụng cho method: PUT, PATCH, DELETE
    public ResponseData(int status, String message) {
        this.status = status;
        this.message = message;
    }

    // sử dụng cho method: GET, POST
    public ResponseData(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
