package com.example.learning_assistant.security.config.jwt;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final Dotenv dotenv = Dotenv.load();

    public String generateToken(String email, String role){
        Map<String, Object> claims = new HashMap<>();
//        claims.put("role","admin"); // for admin
        claims.put("email",email);
        claims.put("role",role);
        return Jwts.builder()
                .claims(claims)   // pass map directly
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + (15 * 24 * 60 * 60 * 1000))) // 15 days
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(System.getenv("JWT_SECRET"));
        byte[] keyBytes = Decoders.BASE64.decode(dotenv.get("JWT_SECRET"));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractEmailFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver){
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) throws SignatureException {

        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmailFromToken(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}