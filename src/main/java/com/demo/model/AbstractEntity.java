package com.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
public abstract class AbstractEntity {

    /**
     * - Annotation @CreationTimestamp này tự động điền giá trị của trường (field) với timestamp hiện tại khi
     * một bản ghi (record) được tạo và lưu vào cơ sở dữ liệu.
     * - Annotation @UpdateTimestamp tự động cập nhật trường (field) với timestamp hiện tại bất cứ khi nào
     * một bản ghi (record) được cập nhật trong cơ sở dữ liệu. ->  theo dõi thời gian cập nhật cuối cùng của một bản ghi
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // lưu thời gian record được tạo ra
    @Column(name = "created_at")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;

    // lưu thời gian record được update lần cuối
    @Column(name = "updated_at")
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateAt;
}
