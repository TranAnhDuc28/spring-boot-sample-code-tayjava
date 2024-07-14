package com.demo.repository.specification;

import com.demo.model.User;
import com.demo.util.Gender;
import org.springframework.data.jpa.domain.Specification;

public class UserSpec {

    public static Specification<User> hasFirstName(String firstName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("firstName"), "%"+ firstName +"%");
    }


    public static Specification<User> notEqualGender(Gender gender) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get("gender"), gender);
    }
}
