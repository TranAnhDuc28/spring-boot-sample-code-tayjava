package com.demo.dto.request;

import com.demo.enums.Platform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SignInRequest implements Serializable {

    @NotBlank(message = "Username must be not null")
    private String username;

    @NotBlank(message = "Password must be not null")
    private String password;

    @NotNull(message = "Password must be not null")
    private Platform platform; // login từ thiết bị nào

    private String deviceToken; // token của thiết bị đăng nhập, sử dụng để xác thực khi đăng nhập lần thứ 2

    private String version; // thông tin version thường có trên mobile
}
