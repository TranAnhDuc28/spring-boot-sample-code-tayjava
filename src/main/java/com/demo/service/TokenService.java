package com.demo.service;

import com.demo.exception.ResourceNotFoundException;
import com.demo.model.Token;
import com.demo.repository.TokenRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public record TokenService(TokenRepository tokenRepository) {
    public int save(Token token) {
        Optional<Token> tokenOptional = tokenRepository.findByUsername(token.getUsername());
        if (tokenOptional.isEmpty()) {
            return tokenRepository.save(token).getId();
        }
        Token currentToken = tokenOptional.get();
        currentToken.setAccessToken(token.getAccessToken());
        currentToken.setRefreshToken(token.getRefreshToken());
        return tokenRepository.save(currentToken).getId();
    }

    public String delete(Token token) {
        tokenRepository.delete(token);
        return "Deleted";
    }

    public Token getByUsername(String username) {
        return tokenRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Token not exists"));
    }
}
