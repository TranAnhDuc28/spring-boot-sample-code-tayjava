package com.demo.service;

import com.demo.dto.common.PageResponse;
import com.demo.dto.reponse.UserDetailResponse;
import com.demo.dto.request.UserRequestDTO;
import com.demo.enums.UserStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {

    UserDetailsService userDetailsService();

    long saveUser(UserRequestDTO request);

    void updateUser(long userId, UserRequestDTO request);

    void changeStatus(long userId, UserStatus status);

    void deleteUser(long userId);

    UserDetailResponse getUser(long userId);

    PageResponse<?> getAllUsersWithSortBy(int pageNo, int pageSize, String sortBy);

    PageResponse<?> getAllUsersWithSortByMultipleColumns(int pageNo, int pageSize, String... sorts);

    PageResponse<?> getAllUsersWithSortByColumnAndSearch(int pageNo, int pageSize, String search, String sortBy);

    PageResponse<?> advanceSearchWithCriteria(int pageNo, int pageSize, String sortBy, String address, String... search);

    PageResponse<?> advanceSearchWithSpecification(Pageable pageable, String[] user, String[] address);
}


