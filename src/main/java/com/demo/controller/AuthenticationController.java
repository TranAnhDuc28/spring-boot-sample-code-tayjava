package com.demo.controller;

import com.demo.dto.reponse.TokenResponse;
import com.demo.dto.request.SignInRequest;
import com.demo.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Validated
@Slf4j
@Tag(name = "Authentication Controller")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/access")
    public ResponseEntity<TokenResponse> login(@RequestBody SignInRequest request) {
        return new ResponseEntity<>(authenticationService.authenticate(request), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(HttpServletRequest request) {
        return new ResponseEntity<>(authenticationService.refresh(request), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public String logout() {
        return "success";
    }

}
