package com.demo.service;

import com.demo.dto.reponse.TokenResponse;
import com.demo.dto.request.SignInRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    TokenResponse authenticate(SignInRequest request);
    TokenResponse refresh(HttpServletRequest request);
}
