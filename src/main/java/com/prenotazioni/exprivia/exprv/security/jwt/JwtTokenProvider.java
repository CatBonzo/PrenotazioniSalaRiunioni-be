package com.prenotazioni.exprivia.exprv.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret:jwtSecretKeyDeveSuperareLunghezzaMinimaPerHS512AlmenoTrentadueBytesLungo}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds:86400000}")
    private long jwtExpirationInMs;

    // Genera token JWT dall'autenticazione (inclusi i ruoli)
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();

        // Estrai i ruoli dall'oggetto Authentication
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(",")); // es: "ROLE_USER,ROLE_ADMIN"

        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationInMs);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles) // 👈 aggiunto il claim con i ruoli
                .issuedAt(currentDate)
                .expiration(expireDate)
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    // Estrae lo username dal token JWT
    public String getUsernameFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    // Estrae i ruoli dal token JWT
    public String getRolesFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("roles", String.class); // es: "ROLE_USER,ROLE_ADMIN"
    }

    // Valida il token JWT
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (SignatureException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
