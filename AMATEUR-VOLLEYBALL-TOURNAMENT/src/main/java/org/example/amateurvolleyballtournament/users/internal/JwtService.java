package org.example.amateurvolleyballtournament.users.internal;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    // In produzione questa chiave deve stare nell'application.properties!
    // Deve essere lunga almeno 256 bit (32 caratteri)
    private static final String SECRET = "chiave_super_segreta_molto_lunga_per_sicurezza_jwt_123456";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)                 // A chi appartiene il token
                .claim("role", role)               // Aggiungi info extra (es. ruolo)
                .setIssuedAt(new Date())           // Quando è stato creato
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // Scade tra 24 ore
                .signWith(KEY, SignatureAlgorithm.HS256) // Firma digitale
                .compact();
    }

    public String getSecretKey() {
        return SECRET;
    }
}
