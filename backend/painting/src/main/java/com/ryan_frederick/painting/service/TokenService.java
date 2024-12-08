package com.ryan_frederick.painting.service;


import com.ryan_frederick.painting.user.User;
import com.ryan_frederick.painting.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.nimbusds.jwt.JWTClaimsSet.*;

@Service
public class TokenService {
    private final JwtEncoder encoder;

    private final JwtDecoder decoder;

    public TokenService(JwtEncoder encoder, JwtDecoder decoder, UserRepository userRepository) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.userRepository = userRepository;
    }

    private final UserRepository userRepository;


    public String generateAuthToken(Authentication authentication) {
        Instant now = Instant.now();
        Set<String> scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        System.out.println(scope);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("authorities", scope)
                .build();
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

    }

    public String generateRefreshToken(Authentication authentication) {
        Optional<User> user = userRepository.findUserByUsername(authentication.getName());
        Integer id;
        id = user.map(User::id).orElse(null);
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.DAYS))
                .subject(authentication.getName())
                .id(String.valueOf(id))
                .build();
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
