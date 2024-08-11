package com.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
/**
 * - @MappedSuperclass: Được sử dụng để định nghĩa một superclass mà các thuộc tính
 * và phương thức của nó sẽ được kế thừa bởi các entity class con.
 * - Không phải là entity: Class được đánh dấu với @MappedSuperclass không phải là một entity
 * và sẽ không có bảng tương ứng trong cơ sở dữ liệu.
 * - Kế thừa các thuộc tính chung: Các entity class kế thừa sẽ tự động có các thuộc tính được khai báo trong superclass.
 */
@MappedSuperclass
public abstract class AbstractEntity<T extends Serializable> implements Serializable{

    /**
     * - Annotation @CreationTimestamp này tự động điền giá trị của trường (field) với timestamp hiện tại khi
     * một bản ghi (record) được tạo và lưu vào cơ sở dữ liệu.
     * - Annotation @UpdateTimestamp tự động cập nhật trường (field) với timestamp hiện tại bất cứ khi nào
     * một bản ghi (record) được cập nhật trong cơ sở dữ liệu. ->  theo dõi thời gian cập nhật cuối cùng của một bản ghi
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private T id;

    // user tạo mới record
//    @CreatedBy
//    @Column(name = "created_by")
//    private T createdBy; // tự động thêm vào userId của người thêm mới
//
//    // user update cuối cùng
//    @LastModifiedBy
//    @Column(name = "updated_by")
//    private T updateBy; // tự động thêm vào userId của người cập nhật cuối cùng
//
//    // lưu thời gian record được tạo ra
    @Column(name = "created_at")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createAt;

    // lưu thời gian record được update lần cuối
    @Column(name = "updated_at")
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updateAt;
}
