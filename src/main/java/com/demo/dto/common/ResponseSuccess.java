package com.demo.dto.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class ResponseSuccess extends ResponseEntity<ResponseSuccess.Payload> {

    // Mô tả dữ liệu trả về  cho API: PUT, PATCH, DELETE
    public ResponseSuccess(HttpStatusCode status, String message) {
        // HttpStatus.OK (<=> 200) không phải là HttpStatusCode status để giải quyết bài toán cho phép in ra data trong phần body
        super(new Payload(status.value(), message), HttpStatus.OK);
    }

    // Mô tả dữ liệu trả về  cho API: GET, POST
    public ResponseSuccess(HttpStatusCode status, String message, Object data) {
        super(new Payload(status.value(), message, data), HttpStatus.OK);
    }

    // lớp Payload giúp đóng gói và vận chuyển dữ liệu một cách hiệu quả và dễ dàng quản lý
    public static class Payload {
        private final int status;
        private final String message;
        private Object data;

        public Payload(int status, String message) {
            this.status = status;
            this.message = message;
        }

        public Payload(int status, String message, Object data) {
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

        public Object getData() {
            return data;
        }
    }

}
