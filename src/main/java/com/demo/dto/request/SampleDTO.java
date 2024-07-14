package com.demo.dto.request;

import lombok.*;

import java.io.Serializable;

/**
 * @Data = @Getter + @Setter + @EqualsAndHashCode + @ToString
 * @Getter => best practive (thực hành tốt nhất), DTO chỉ cần getter là đủ
 * @Builder => best practive (thực hành tốt nhất), khuyến khích dùng builder
 * @RequiredArgsConstructor => tự động tạo ra một constructor với các tham số cho tất cả các fields final
 * và các trường đánh dấu là @NonNull
 * @Slf4j => được sử dụng để tự động thêm một logger vào lớp, giúp dễ dàng ghi log mà không cần phải tự mình tạo và quản lý đối tượng logger.
 */

@Getter(AccessLevel.PROTECTED) // AccessLevel chỉ định phạm vi truy xuất cho method get
@AllArgsConstructor(access = AccessLevel.MODULE) // <=> @RequiredArgsConstructor
public class SampleDTO implements Serializable {

    private Integer id;
    private String name;
}
