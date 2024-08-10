package com.demo.service.impl;

import com.demo.dto.reponse.TokenResponse;
import com.demo.dto.request.SignInRequest;
import com.demo.exception.InvalidDataException;
import com.demo.repository.UserRepository;
import com.demo.service.AuthenticationService;
import com.demo.service.JwtService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public TokenResponse authenticate(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username or password is incorrect"));

        String accessToken = jwtService.generateToken(user);

        String refreshToken = jwtService.generateRefreshToken(user);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    @Override
    public TokenResponse refresh(HttpServletRequest request) {
        String token = request.getHeader("x-token");
        if(StringUtils.isBlank(token)) throw new InvalidDataException("Token must be not blank.");

        // extract user from token
        final String username = jwtService.extractUsername(token);
        System.out.println("username: " + username);


        // check it into db

        return null;
    }
}
