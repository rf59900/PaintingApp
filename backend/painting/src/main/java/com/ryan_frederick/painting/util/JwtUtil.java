package com.ryan_frederick.painting.util;

import com.ryan_frederick.painting.painting.PaintingController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    Logger logger = LogManager.getLogger(JwtUtil.class);
    private final JwtDecoder decoder;

    public JwtUtil(JwtDecoder decoder) {
        this.decoder = decoder;
    }

    public String getUsernameFromToken(String jwtToken) throws BadJwtException {
        Jwt jwt = decoder.decode(jwtToken);
        logger.info(jwt);
        return jwt.getClaimAsString("sub");
    }

    public boolean validateJwt(String jwtToken) throws BadJwtException {
        Jwt jwt = decoder.decode(jwtToken);
        return jwt.getExpiresAt().isAfter(new Date().toInstant());
    }

    public String getClaims(String jwtToken) throws BadJwtException {
        Jwt jwt = decoder.decode(jwtToken);
        return jwt.getClaims().toString();
    }
}
