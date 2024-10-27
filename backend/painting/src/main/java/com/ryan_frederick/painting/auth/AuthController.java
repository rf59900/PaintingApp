package com.ryan_frederick.painting.auth;

import com.ryan_frederick.painting.service.TokenService;

import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
public class AuthController {
    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(Authentication authentication) {
        // create auth token and refresh token
        String authToken = tokenService.generateAuthToken(authentication);
        String refreshToken = tokenService.generateRefreshToken(authentication);

        // create http only cookie containing the refresh token with a duration of 1 day
        ResponseCookie jwtCookie = ResponseCookie.from("jwt")
                .value(refreshToken)
                .domain("localhost")
                .maxAge(Duration.ofDays(1))
                .httpOnly(true)
                .secure(true)
                .build();

        // set cookie and return auth token in the body
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(authToken);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout() {
        // create blank cookie that expires instantly
        ResponseCookie jwtCookie = ResponseCookie.from("jwt")
                .value("")
                .domain("localhost")
                .maxAge(Duration.ofSeconds(0))
                .httpOnly(true)
                .secure(true)
                .build();

        // replace refresh token cookie with blank cookie
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(null);

    }
}
