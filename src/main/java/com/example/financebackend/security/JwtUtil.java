package com.example.financebackend.security;

import com.example.financebackend.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

  private final SecretKey key;
  private final long expirationMs;
  private final String issuer;

  public JwtUtil(
      @Value("${app.jwt.secret}") String secret,
      @Value("${app.jwt.expiration-ms}") long expirationMs,
      @Value("${app.jwt.issuer}") String issuer
  ) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expirationMs = expirationMs;
    this.issuer = issuer;
  }

  public String generateToken(String email, Role role) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + expirationMs);

    // JJWT 0.12+: sign with SecretKey only (no separate algorithm arg).
    return Jwts.builder()
        .issuer(issuer)
        .subject(email)
        .claim("role", role.name())
        .issuedAt(now)
        .expiration(expiry)
        .signWith(key)
        .compact();
  }

  public boolean isTokenValid(String token) {
    try {
      Claims claims = parseClaims(token);
      Date expiration = claims.getExpiration();
      return expiration != null && expiration.after(new Date());
    } catch (Exception ex) {
      return false;
    }
  }

  public String extractEmail(String token) {
    return parseClaims(token).getSubject();
  }

  public Role extractRole(String token) {
    String roleValue = parseClaims(token).get("role", String.class);
    return Role.valueOf(roleValue);
  }

  private Claims parseClaims(String token) {
    return Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}

