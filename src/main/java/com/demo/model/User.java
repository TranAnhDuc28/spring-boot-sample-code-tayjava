package com.demo.model;

import com.demo.util.Gender;
import com.demo.util.UserStatus;
import com.demo.util.UserType;
import com.demo.validator.PhoneNumber;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.jdi.LongValue;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_user")
public class User extends AbstractEntity {

    /**
     *  - Annotation @Enumerated(EnumType.STRING) này cho phép bạn lưu các giá trị enum dưới dạng chuỗi (string)
     *  thay vì số nguyên (ordinal) trong cơ sở dữ liệu.
     *  + EnumType.STRING: Lưu trữ giá trị enum dưới dạng chuỗi.
     *  + EnumType.ORDINAL: Lưu trữ giá trị enum dưới dạng số nguyên, tương ứng với thứ tự của các giá trị trong enum (bắt đầu từ 0).
     *
     *  - Annotation @JdbcTypeCode(SqlTypes.NAMED_ENUM) sử dụng để chỉ định cách Hibernate nên ánh xạ
     *  (map) một kiểu dữ liệu enum vào cơ sở dữ liệu. Annotation này giúp Hibernate hiểu rằng cột này
     *  trong cơ sở dữ liệu nên được ánh xạ với một kiểu dữ liệu enum cụ thể.
     *  + SqlTypes.NAMED_ENUM là một kiểu dữ liệu đặc biệt trong Hibernate, cho phép ánh xạ một enum Java
     *  vào một kiểu enum tương ứng trong cơ sở dữ liệu.
     */

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "gender")
    private Gender gender;

    @PhoneNumber
    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status")
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "type")
    private UserType type;

    private Integer age;

    private Boolean activated;

//    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    private Set<Address> addresses = new HashSet<>();

    public void saveAddress(Address address) {
        if(address != null) {
            if(addresses == null) {
                addresses = new HashSet<>();
            }
            addresses.add(address);
            address.setUser(this); // save user id
        }
    }
}
