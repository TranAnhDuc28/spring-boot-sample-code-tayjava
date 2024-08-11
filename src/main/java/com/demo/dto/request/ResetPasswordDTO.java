package com.demo.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ResetPasswordDTO implements Serializable {
    private String secretKey;
    private String password;
    private String confirmPassword;

}
