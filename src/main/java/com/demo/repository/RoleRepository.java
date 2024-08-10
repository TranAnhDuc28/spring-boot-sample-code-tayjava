package com.demo.repository;

import com.demo.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    @Query(value = """
        select r from Role r inner join UserHasRole ur on r.id = ur.role.id
        where ur.user.id = :userId
            """)
    List<Role> findAllByUserId(Long userId);
}
