package com.demo.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum UserStatus {

    // @JsonProperty cho phép nhập dữ liệu dạng dữ liệu được khai báo trong ()
    @JsonProperty("active")
    ACTIVE,

    @JsonProperty("inactive")
    INACTIVE,

    @JsonProperty("none")
    NONE
}
