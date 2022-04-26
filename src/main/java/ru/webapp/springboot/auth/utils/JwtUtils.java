package ru.webapp.springboot.auth.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.webapp.springboot.auth.entity.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

@Component
@Log
public class JwtUtils {

    public static final String CLAIM_USER_KEY = "user";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access_token-expiration}")
    private int accessTokenExpiration;

    @Value("${jwt.reset-pass-expiration}")
    private int resetPassTokenExpiration;

    public String createAccessToken(User user) {
        return createToken(user, accessTokenExpiration);
    }

    public String createEmailResetToken(User user) {
        return createToken(user, resetPassTokenExpiration);
    }

    private String createToken(User user, int duration) {

        Date currentDate = new Date();

        user.setPassword(null);

        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_USER_KEY, user);
        claims.put(Claims.SUBJECT, user.getId());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(currentDate)
                .setExpiration(new Date(currentDate.getTime() + duration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public boolean validate(String jwt) {

        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt);
            return true;
        } catch (MalformedJwtException e) {
            log.log(Level.SEVERE, "Invalid JWT token: ", jwt);
        } catch (ExpiredJwtException e) {
            log.log(Level.SEVERE, "JWT token is expired: ", jwt);
        } catch (UnsupportedJwtException e) {
            log.log(Level.SEVERE, "JWT token is unsupported: ", jwt);
        } catch (IllegalArgumentException e) {
            log.log(Level.SEVERE, "JWT claims string is empty: ", jwt);
        }

        return false;
    }

    public User getUser(String jwt) {

        Map<?, ?> map = (Map<?, ?>) Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).getBody().get(CLAIM_USER_KEY);

        ObjectMapper mapper = new ObjectMapper();

        return mapper.convertValue(map, User.class);
    }

}
