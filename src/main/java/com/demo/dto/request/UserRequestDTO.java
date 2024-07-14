package com.demo.dto.request;

import com.demo.util.*;
import com.demo.validator.EnumPattern;
import com.demo.validator.EnumValue;
import com.demo.validator.GenderSubset;
import com.demo.validator.PhoneNumber;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import static com.demo.util.Gender.*;

@Getter
public class UserRequestDTO implements Serializable {

    @NotBlank(message = "firstName must be not blank")
    private String firstName;

    @NotNull(message = "lastName must be not null")
    private String lastName;

//    @Pattern(regexp = "^\\d{10}$", message = "phone invalid format")
    @PhoneNumber(message = "phone invalid format")
    private String phone;

    @Email(message = "Email invalid format")
    private String email;

    @NotNull(message = "dateOfBirth must be not null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // định nghĩa kiểu date
    @JsonFormat(pattern = "MM/dd/yyyy") // nhận về giá trị kiểu MM/dd/yyyy
    private Date dateOfBirth;

//    @Pattern(regexp = "^ACTIVE|INACTIVE|NONE$", message = "Status must be one in {ACTIVE, INACTIVE, NONE}")
    @EnumPattern(name = "status", regexp = "ACTIVE|INACTIVE|NONE")
    private UserStatus status;

    @GenderSubset(anyOf = {MALE, FEMALE, OTHER})
    private Gender gender;

    @NotNull(message = "type must be not null")
    @EnumValue(name = "type", enumClass = UserType.class)
    private String type;

    @NotNull(message = "User name must be not null")
    private String username;

    @NotNull(message = "Pass word must be not null")
    private String password;

    @NotEmpty(message = "Addresses can not empty")
    private Set<AddressDTO> addresses;

    public UserRequestDTO(String firstName, String lastName, String phone, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
    }
}
