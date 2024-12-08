package com.ryan_frederick.painting.auth;

import com.ryan_frederick.painting.painting.PaintingController;
import com.ryan_frederick.painting.service.CustomUserDetailsService;
import com.ryan_frederick.painting.service.TokenService;

import com.ryan_frederick.painting.user.User;
import com.ryan_frederick.painting.user.UserRepository;
import com.ryan_frederick.painting.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Optional;

@RestController
public class AuthController {
    private final JwtDecoder jwtDecoder;
    private final TokenService tokenService;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;

    Logger logger = LogManager.getLogger(AuthController.class);
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public AuthController(JwtDecoder jwtDecoder, TokenService tokenService, CustomUserDetailsService customUserDetailsService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtDecoder = jwtDecoder;
        this.tokenService = tokenService;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse> login(Authentication authentication) {
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

        // encode refresh token
        String encodedRefreshToken = passwordEncoder.encode(refreshToken);

        // store encoded refresh token in db
        userRepository.updateUserRefreshToken(authentication.getName(), encodedRefreshToken);

        // set cookie and return auth token in the body
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new AuthTokenResponse(authToken));
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(Authentication authentication) {
        // create blank cookie that expires instantly
        ResponseCookie jwtCookie = ResponseCookie.from("jwt")
                .value("")
                .domain("localhost")
                .maxAge(Duration.ofSeconds(0))
                .httpOnly(true)
                .secure(true)
                .build();

        // remove refresh token from db
        userRepository.updateUserRefreshToken(authentication.getName(), null);

        // replace refresh token cookie with blank cookie
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"message\":\"User has logged out\"}");

    }

    @GetMapping("/refresh")
    public ResponseEntity<AuthTokenResponse> refresh(@CookieValue("jwt") String reqRefreshToken) {
        if (reqRefreshToken == null) {
            return ResponseEntity.badRequest()
                    .body(new AuthTokenResponse("INVALID"));
        }
        logger.info("Here");
        logger.info("token = " + reqRefreshToken );
        String username = jwtUtil.getUsernameFromToken(reqRefreshToken);
        logger.info("refresh username = " + username);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        logger.info("Here 4");
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // find refresh token stored in db
        Optional<User> foundUser = userRepository.findUserByUsername(username);
        String foundRefreshToken = foundUser.map(User::refreshToken).orElse(null);

        logger.info("Here 1");
        // if db refresh token is same as req refresh token send new auth and refresh token
        if (passwordEncoder.matches(reqRefreshToken, foundRefreshToken)) {
            String authToken = tokenService.generateAuthToken(authentication);
            String refreshToken = tokenService.generateRefreshToken(authentication);

            ResponseCookie jwtCookie = ResponseCookie.from("jwt")
                    .value(refreshToken)
                    .domain("localhost")
                    .maxAge(Duration.ofDays(1))
                    .httpOnly(true)
                    .secure(true)
                    .build();
            logger.info("Here 2");
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(new AuthTokenResponse(authToken));
        } else {
            return ResponseEntity.badRequest()
                    .body(new AuthTokenResponse("INVALID"));
        }




    }
}
