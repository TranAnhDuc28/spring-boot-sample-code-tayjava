package com.demo.service.impl;

import com.demo.dto.reponse.TokenResponse;
import com.demo.dto.request.ResetPasswordDTO;
import com.demo.dto.request.SignInRequest;
import com.demo.exception.InvalidDataException;
import com.demo.model.Token;
import com.demo.model.User;
import com.demo.repository.UserRepository;
import com.demo.service.AuthenticationService;
import com.demo.service.JwtService;
import com.demo.service.TokenService;
import com.demo.service.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.header.Header;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.demo.enums.TokenType.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public TokenResponse accessToken(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        var user = userService.getUserByUsername(request.getUsername());
        if(!user.isEnabled()) throw new InvalidDataException("User not active");

        String accessToken = jwtService.generateAccessToken(user);

        String refreshToken = jwtService.generateRefreshToken(user);

        // save token to db
        tokenService.save(Token.builder()
                .username(user.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    @Override
    public TokenResponse refreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader(HttpHeaders.REFERER);
        if (StringUtils.isBlank(refreshToken)) throw new InvalidDataException("Token must be not blank.");

        // extract user from token
        final String username = jwtService.extractUsername(refreshToken, REFRESH_TOKEN);

        // check it into db
        var user = userService.getUserByUsername(username);
        if (!jwtService.isValidToken(refreshToken, REFRESH_TOKEN, user)) {
            throw new InvalidDataException("Not allow access with this token");
        }

        // create new access token
        String accessToken = jwtService.generateAccessToken(user);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    @Override
    public String removeToken(HttpServletRequest request) {
        String refreshToken = request.getHeader(HttpHeaders.REFERER);
        if (StringUtils.isBlank(refreshToken)) throw new InvalidDataException("Token must be not blank.");

        // extract user from token
        final String username = jwtService.extractUsername(refreshToken, ACCESS_TOKEN);

        // check token into db
        Token currentToken = tokenService.getByUsername(username);

        // delete token
        tokenService.delete(currentToken);

        return "Deleted";
    }

    @Override
    public String forgotPassword(String email) {
        log.info("---------- forgotPassword ----------");

        // check email exists
        User user = userService.getUserByEmail(email);

        // user is active or inactive
        if(!user.isEnabled()) throw new InvalidDataException("User not active");

        // generate reset token
        String resetToken = jwtService.generateResetToken(user);

        // save to db
//        tokenService.save(Token.builder().username(user.getUsername()).resetToken(resetToken).build());

        // send email confirm link
        String confirmLink = String.format(
                "curl --location 'http://localhost:80/auth/reset-password' \\\n" +
                "--header 'accept: */*' \\\n" +
                "--header 'Content-Type: application/json' \\\n" +
                "--data '%s'", resetToken);
        log.info("--> confirmLink: {}", confirmLink);

        return "Send";
    }

    @Override
    public String resetPassword(String resetKey) {
        log.info("---------- resetPassword ----------");

        // validate token
        User user = this.validateToken(resetKey);

        // check token by username
        tokenService.getByUsername(user.getUsername());

        if (!jwtService.isValidToken(resetKey, RESET_TOKEN, user)) {
            throw new InvalidDataException("Not allow access with this token");
        }
        return "Reset";
    }

    @Override
    public String changePassword(ResetPasswordDTO request) {
        log.info("---------- changePassword ----------");

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new InvalidDataException("Passwords do not match");
        }

        // get user by reset token
        User user = this.validateToken(request.getSecretKey());


        // update password
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userService.saveUser(user);

        return "Changed";
    }

    private User validateToken(String token) {
        // extract username to token
        final String username = jwtService.extractUsername(token, RESET_TOKEN);

        // validate user is active or not
        var user = userService.getUserByUsername(username);
        if(!user.isEnabled()) throw new InvalidDataException("User not active");

        return user;
    }
}
