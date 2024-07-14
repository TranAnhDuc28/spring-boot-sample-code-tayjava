package com.demo.service.impl;

import com.demo.dto.reponse.PageResponse;
import com.demo.dto.reponse.UserDetailResponse;
import com.demo.dto.request.AddressDTO;
import com.demo.dto.request.UserRequestDTO;
import com.demo.exception.ResourceNotFoundException;
import com.demo.model.Address;
import com.demo.model.User;
import com.demo.repository.SearchRepository;
import com.demo.repository.UserRepository;
import com.demo.repository.specification.UserSpecificationsBuilder;
import com.demo.service.UserService;
import com.demo.util.UserStatus;
import com.demo.util.UserType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SearchRepository searchRepository;

    @Override
    public long saveUser(UserRequestDTO request) {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phone(request.getPhone())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .status(request.getStatus())
                .type(UserType.valueOf(request.getType().toUpperCase()))
                .build();
        request.getAddresses().forEach(a ->
                user.saveAddress(Address.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build()));

        userRepository.save(user);
        log.info("User add save!");
        return user.getId();
    }

    @Override
    public void updateUser(long userId, UserRequestDTO request) {
        User user = getUserById(userId);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setPhone(request.getPhone());
        if (!request.getEmail().equals(user.getEmail())) {
            // check email from database if exist then allow update email otherwise throw exception
            user.setEmail(request.getEmail());
        }
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setStatus(request.getStatus());
        user.setType(UserType.valueOf(request.getType().toUpperCase()));
        user.setAddresses(convertToAddress(request.getAddresses()));
        userRepository.save(user);
        log.info("User updated successfully");
    }

    @Override
    public void changeStatus(long userId, UserStatus status) {
        User user = getUserById(userId);
        user.setStatus(status);
        userRepository.save(user);
        log.info("Status changed");
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
        log.info("User deleted, userId = {}", userId);
    }

    @Override
    public UserDetailResponse getUser(long userId) {
        User user = getUserById(userId);
        return UserDetailResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }

    @Override
    public PageResponse<?> getAllUsersWithSortBy(int pageNo, int pageSize, String sortBy) {
        int page = 0;
        if (pageNo > 0) {
            page = pageNo - 1;
        }


        List<Sort.Order> orders = new ArrayList<>();

        // nếu có giá trị
        if (StringUtils.hasLength(sortBy)) {
            // giá trị đúng -> firstName:asc|desc
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)"); // trong dấu () là 1 group
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                } else {
                    orders.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }
        }

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(orders));

        Page<User> users = userRepository.findAll(pageable);

        List<UserDetailResponse> response = users.stream().map(user -> UserDetailResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build())
                .collect(Collectors.toList());

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(users.getTotalPages())
                .items(response)
                .build();
    }

    @Override
    public PageResponse<?> getAllUsersWithSortByMultipleColumns(int pageNo, int pageSize, String... sorts) {
        int page = 0;
        if (pageNo > 0) {
            page = pageNo - 1;
        }

        List<Sort.Order> orders = new ArrayList<>();

        if (sorts != null) {
            for (String sortBy : sorts) {
                log.info("sortBy: {}", sortBy);
                // giá trị đúng -> firstName:asc|desc
                Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)"); // trong dấu () là 1 group
                Matcher matcher = pattern.matcher(sortBy);
                if (matcher.find()) {
                    if (matcher.group(3).equalsIgnoreCase("asc")) {
                        orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                    } else {
                        orders.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                    }
                }
            }
        }

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(orders));

        Page<User> users = userRepository.findAll(pageable);

        List<UserDetailResponse> response = users.stream().map(user -> UserDetailResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build())
                .collect(Collectors.toList());

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(users.getTotalPages())
                .items(response)
                .build();
    }

    @Override
    public PageResponse<?> getAllUsersWithSortByColumnAndSearch(int pageNo, int pageSize, String search, String sortBy) {
        return searchRepository.getAllUsersWithSortByColumnAndSearch(pageNo, pageSize, search, sortBy);
    }

    @Override
    public PageResponse<?> advanceSearchWithCriteria(int pageNo, int pageSize, String sortBy, String address, String... search) {
        return searchRepository.searchUserByCriteria(pageNo, pageSize, sortBy, address, search);
    }

    @Override
    public PageResponse<?> advanceSearchWithSpecification(Pageable pageable, String[] user, String[] address) {
        Page<User> users = null;
        List<User> list = new ArrayList<>();

        if (user != null && address != null) {
            // tìm kiếm trên user và address --> join table
            return searchRepository.getUsersJoinedAdress(pageable, user, address);


        } else if (user != null) {
            // tìm kiếm trên bảng user
            // Specification hỗ trợ build câu lệnh tìm kiếm
//            Specification<User> firstNameSpec = UserSpec.hasFirstName("T");
//            Specification<User> genderSpec = UserSpec.notEqualGender(Gender.MALE);
//
//            Specification<User> finalSpec = firstNameSpec.and(genderSpec);
            UserSpecificationsBuilder builder = new UserSpecificationsBuilder();

            for (String s : user) {
                Pattern pattern = Pattern.compile("(\\w+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)");
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                }
            }

            list = userRepository.findAll(builder.build());
            return PageResponse.builder()
                    .pageNo(pageable.getPageNumber())
                    .pageSize(pageable.getPageSize())
                    .totalPages(10)
                    .items(list)
                    .build();
        } else {
            users = userRepository.findAll(pageable);
        }

        return PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPages(users.getTotalPages())
                .items(list)
                .build();
    }


    private Set<Address> convertToAddress(Set<AddressDTO> addresses) {
        Set<Address> result = new HashSet<>();
        addresses.forEach(a ->
                result.add(Address.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build()));
        return result;
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
