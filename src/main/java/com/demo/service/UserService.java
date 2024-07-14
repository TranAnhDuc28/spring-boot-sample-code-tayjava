package com.demo.service;

import com.demo.dto.reponse.PageResponse;
import com.demo.dto.reponse.UserDetailResponse;
import com.demo.dto.request.UserRequestDTO;
import com.demo.util.UserStatus;
import org.springframework.data.domain.Pageable;

public interface UserService {

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


