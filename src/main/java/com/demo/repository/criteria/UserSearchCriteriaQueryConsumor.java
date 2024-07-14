package com.demo.repository.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Consumer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchCriteriaQueryConsumor implements Consumer<SearchCriteria> {

    /**
     * - Khi muốn sử dụng Predicate để lọc dữ liệu, có thể cần sử dụng một Consumer<Predicate>
     * để thêm các điều kiện vào truy vấn một cách linh hoạt. Một Consumer<Predicate> là
     * một functional interface trong Java, nhận một đối tượng Predicate và thực hiện một thao tác nào đó trên đối tượng đó.
     * - Sử dụng Consumer<Predicate> có thể xây dựng các điều kiện truy vấn một cách linh hoạt
     * và dễ dàng mở rộng hoặc thay đổi logic mà không làm ảnh hưởng đến các phần khác của mã nguồn.
     */

    private Predicate predicate; // đối tượng chỉ định
    private CriteriaBuilder builder;
    private Root root;

    @Override
    public void accept(SearchCriteria param) {
        if (param.getOperation().equalsIgnoreCase(">")) {
            predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get(param.getKey()), param.getValue().toString()));
        } else if (param.getOperation().equalsIgnoreCase("<")) {
            predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get(param.getKey()), param.getValue().toString()));
        } else if (param.getOperation().equalsIgnoreCase(":")){
            if(root.get(param.getKey()).getJavaType() == String.class) {
                predicate = builder.and(predicate, builder.like(root.get(param.getKey()), "%" + param.getValue().toString() + "%"));
            } else {
                predicate = builder.and(predicate, builder.equal(root.get(param.getKey()), param.getValue().toString()));
            }
        }
    }
}
