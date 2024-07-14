package com.demo.repository.specification;

import lombok.Getter;

@Getter
public class SpecSearchCriteria {

    private String key;
    private SearchOperation operation;
    private Object value;
    private Boolean orPredicate;

    public SpecSearchCriteria (String key, SearchOperation operation, Object value) {
        super();
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public SpecSearchCriteria (String orPredicate, String key, SearchOperation operation, Object value) {
        super();
        this.orPredicate = orPredicate != null && orPredicate.equals(SearchOperation.OR_PREDICATE_FLAG);
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public SpecSearchCriteria(String key, String operation, String value, String prefix, String suffix) {
        SearchOperation oper = SearchOperation.getSimpleOperation(operation.charAt(0)); // lấy ra toán tử
        if (oper == SearchOperation.EQUALITY) { // kiểm tra nếu nó là toán tử =
            // kiểm tra xem tiền tố và hậu tố có chứa dấu *
            boolean startWithAsterisk = prefix != null && prefix.contains(SearchOperation.ZERO_OR_MORE_REGEX);
            boolean endWithAsterisk = suffix != null && suffix.contains(SearchOperation.ZERO_OR_MORE_REGEX);

            if (startWithAsterisk && endWithAsterisk) {
                oper = SearchOperation.CONTAINS;
            } else if (startWithAsterisk) { // bắt đầu bằng dấu * -> toán tử ENDS_WITH
                oper = SearchOperation.ENDS_WITH;
            } else if (endWithAsterisk) { // kết thúc bằng dấu * -> toán tử STARTS_WITH
                oper = SearchOperation.STARTS_WITH;
            }
        }

        this.key = key;
        this.operation = oper;
        this.value = value;
    }
}
