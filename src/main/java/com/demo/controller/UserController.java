package com.demo.controller;

import com.demo.configuration.Translator;
import com.demo.dto.reponse.ResponseData;
import com.demo.dto.reponse.ResponseError;
import com.demo.dto.reponse.UserDetailResponse;
import com.demo.dto.request.UserRequestDTO;
import com.demo.exception.ResourceNotFoundException;
import com.demo.service.UserService;
import com.demo.util.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Validated
@Slf4j
@Tag(name = "User Controller")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // @Operation giúp định nghĩa các đặc tả chi tiết cho từng phương thức xử lý (handler methods) của API.
    @Operation(method = "POST", summary = "Add new user", description = "Send a request via this API to create new user", responses = {
            @ApiResponse(responseCode = "201", description = "User added successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "ex name", summary = "ex summary",
                                    value = """
                                            {
                                                "status": 201,
                                                "message": "User added successfully",
                                                "data": 1
                                            }
                                            """
                            )))
    })
    @PostMapping(value = "/")
    public ResponseData<Long> addUser(@Valid @RequestBody UserRequestDTO userDTO) {
        log.info("Request add user = {} {}", userDTO.getFirstName(), userDTO.getLastName());

        try {
            long userId = userService.saveUser(userDTO);
            return new ResponseData<>(HttpStatus.CREATED.value(), Translator.toLocale("user.add.success"), userId);
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Add user fail");
        }

    }

    @Operation(summary = "Update user", description = "Send a request via this API to update user")
    @PutMapping("/{userId}")
    public ResponseData<?> updateUser(@PathVariable @Min(1) long userId,
                                      @Valid @RequestBody UserRequestDTO user) {
        log.info("Request update userId = {}", userId);

        try {
            userService.updateUser(userId, user);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.update.success"));
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update user fail");
        }

    }

    @Operation(summary = "Change status of user", description = "Send a request via this API to change status of user")
    @PatchMapping("/{userId}")
    public ResponseData<?> changeStatus(@Min(1) @PathVariable int userId,
                                        @RequestParam UserStatus status) {
        log.info("Request change user status, userId = {}", userId);

        try {
            userService.changeStatus(userId, status);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.change.success"));
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Change user fail");
        }
    }


    @Operation(summary = "Delete user permanently", description = "Send a request via this API to delete user permanently")
    @DeleteMapping("/{userId}")
    public ResponseData<?> deleteUser(@PathVariable @Min(value = 1, message = "userId must be greater than 0") int userId) {
        log.info("Request delete userId = {}", userId);

        try {
            userService.deleteUser(userId);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.del.success"));
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Change user fail");
        }
    }


    @Operation(summary = "Get user detail", description = "Send a request via this API to get user information")
    @GetMapping("/{userId}")
    public ResponseData<UserDetailResponse> getUser(@Min(1) @PathVariable long userId) {
        log.info("Request get user detail by userId = {}", userId);
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "User", userService.getUser(userId));
        } catch (ResourceNotFoundException e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }


    @Operation(summary = "Get list of per pageNo and sort by column",
            description = "Send a request via this API to get user list by pageNo and pageSize and sort by column")
    @GetMapping("/list")
    public ResponseData<?> getAllUsers(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                       @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize,
                                       @RequestParam(required = false) String sortBy) {
        log.info("Request get all of users with sort by column");
        return new ResponseData<>(HttpStatus.OK.value(), "Users", userService.getAllUsersWithSortBy(pageNo, pageSize, sortBy));
    }


    @Operation(summary = "Get list of users per pageNo with sort by multiple columns",
            description = "Send a request via this API to get user list by pageNo and pageSize and sort by multiple columns")
    @GetMapping("/list-with-sort-by-multiple-columns")
    public ResponseData<?> getAllUsersWithSortByMultipleColumns(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                                @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                                @RequestParam(required = false) String... sorts) {
        log.info("Request get user list with paging, sort by multiple columns");
        return new ResponseData<>(HttpStatus.OK.value(), "Users", userService.getAllUsersWithSortByMultipleColumns(pageNo, pageSize, sorts));
    }


    @Operation(summary = "Get list of users and search with paging and sorting by customize query",
            description = "Send a request via this API to get user list by pageNo, pageSize and sort by multiple columns")
    @GetMapping("/list-with-sort-by-column-and-search")
    public ResponseData<?> getAllUsersWithSortByColumnAndSearch(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                                         @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                                         @RequestParam(required = false) String search,
                                                                         @RequestParam(required = false) String sortBy) {
        log.info("Request get user list with paging, sort and search customize query");
        return new ResponseData<>(HttpStatus.OK.value(), "Users", userService.getAllUsersWithSortByColumnAndSearch(pageNo, pageSize, search, sortBy));
    }


    @Operation(summary = "Advance search query with criteria",
            description = "Send a request via this API to get user list by pageNo and pageSize, sort by column and advance search")
    @GetMapping("/advance-search-with-criteria")
    public ResponseData<?> advanceSearchWithCriteria(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                                @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                                @RequestParam(required = false) String sortBy,
                                                                @RequestParam(required = false) String address,
                                                                @RequestParam(defaultValue = "") String... search) {
        log.info("Request get list of users paging, sorting and advance search with criteria");
         return new ResponseData<>(HttpStatus.OK.value(), "Users", userService.advanceSearchWithCriteria(pageNo, pageSize, sortBy, address, search));
    }


    @Operation(summary = "Advance search query with specification",
            description = "Send a request via this API to get user list by pageNo and pageSize, sort by column and advance search")
    @GetMapping("/advance-search-with-specification")
    public ResponseData<?> advanceSearchBySpecification(Pageable pageable,
                                                   @RequestParam(required = false) String[] user,
                                                   @RequestParam(required = false) String[] address) {
        log.info("Request advance search with criteria and paging and sorting");
        return new ResponseData<>(HttpStatus.OK.value(), "Users", userService.advanceSearchWithSpecification(pageable, user, address));
    }
}
