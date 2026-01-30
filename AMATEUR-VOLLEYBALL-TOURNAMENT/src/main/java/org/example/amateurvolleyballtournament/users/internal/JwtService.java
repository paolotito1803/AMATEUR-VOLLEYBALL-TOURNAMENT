package org.example.amateurvolleyballtournament.users.internal;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    public String generateToken(String email, String role) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes());

        return Jwts.builder()
                .setSubject(email)                 // Who owns the token
                .claim("role", role)               // Extra info (role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // Valid for 24 hours
                .signWith(key, SignatureAlgorithm.HS256) // digital signature
                .compact();
    }

    public String getSecretKey() {
        return secret;
    }
}
