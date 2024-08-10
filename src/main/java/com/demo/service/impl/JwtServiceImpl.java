package com.demo.service.impl;

import com.demo.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiryHour}")
    private long expiryHour;

    @Value("${jwt.expiryDay}")
    private long expiryDay;

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, true);
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, false);
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public boolean isValidToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);

        return username.equals(userDetails.getUsername());
    }

    private String generateToken(Map<String, Object> claims, UserDetails userDetails, boolean accessOrRefreshToken) {
        long expiration;
        if (accessOrRefreshToken) {
            expiration = expiryHour; // thời gian hết hạn access token
        } else {
            expiration = expiryDay; // thời gian hết hạn refresh token
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis())) // ngày tạo token
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * expiration)) // thời gian hết hạn
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey() {
        byte[] key = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
