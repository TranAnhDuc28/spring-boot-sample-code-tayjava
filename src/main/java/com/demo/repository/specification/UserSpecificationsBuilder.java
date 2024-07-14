package com.demo.repository.specification;

import com.demo.model.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecificationsBuilder {

    private final List<SpecSearchCriteria> params;

    public UserSpecificationsBuilder() {
        this.params = new ArrayList<>();
    }

    public UserSpecificationsBuilder with(String key, String operation, Object value, String prefix, String suffix) {
        return with(null, key, operation, value, prefix, suffix);
    }

    public UserSpecificationsBuilder with(String orPredicate, String key, String operation, Object value, String prefix, String suffix) {
        SearchOperation oper = SearchOperation.getSimpleOperation(operation.charAt(0));
        if (oper == SearchOperation.EQUALITY) {
            boolean startWithAsterisk = prefix != null && prefix.contains(SearchOperation.ZERO_OR_MORE_REGEX);
            boolean endWithAsterisk = suffix != null && suffix.contains(SearchOperation.ZERO_OR_MORE_REGEX);

            if (startWithAsterisk && endWithAsterisk) {
                oper = SearchOperation.CONTAINS;
            } else if (startWithAsterisk) {
                oper = SearchOperation.ENDS_WITH;
            } else if (endWithAsterisk) {
                oper = SearchOperation.STARTS_WITH;
            }
        }

        params.add(new SpecSearchCriteria(orPredicate, key, oper, value));
        return this;
    }

    public Specification<User> build() {
        if (params.isEmpty()) return null;

        Specification<User> specification = new UserSpecification(params.get(0));

        for (int i = 1; i < params.size(); i++) {
            specification = params.get(i).getOrPredicate()
                    ? Specification.where(specification).or(new UserSpecification(params.get(i)))
                    : Specification.where(specification).and(new UserSpecification(params.get(i)));
        }
        return specification;
    }

    public UserSpecificationsBuilder with(UserSpecification spec) {
        params.add(spec.getCriteria());
        return this;
    }

    public UserSpecificationsBuilder with(SpecSearchCriteria criteria) {
        params.add(criteria);
        return this;
    }
}
