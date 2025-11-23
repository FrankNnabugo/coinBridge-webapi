package com.example.paymentApi.users;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String jwtSecret;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    @Value("${security.jwt.refresh-secret}")
    private String refreshSecret;

    @Value("${security.jwt.refresh-expiration-time}")
    private long refreshExpiryTime;

    public String generateAccessToken(User user){
        HashMap<String, Object> extraClaims = new HashMap<>();
        return generateToken(extraClaims, user);
    }

    public String generateToken(Map<String, Object> extraClaims, User user){
        return buildToken(extraClaims, user, jwtExpiration);
    }

    public String buildToken(Map<String, Object> extraClaims,
                             User userDetail,
                             long expiration){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetail.getId())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

//    public boolean isTokenValid(String token, User user) {
//        final String userId = extractSubject(token);
//        return (userId.equals(user.getId())) && !isTokenExpired(token);
//    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key signRefreshTokenWithKey() {
        byte[] keyBytes = Decoders.BASE64.decode(refreshSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

        public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmailAddress())
                .claim("id", user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiryTime ))
                .signWith(signRefreshTokenWithKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signRefreshTokenWithKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        }
        catch (JwtException e) {
            return false;
        }
    }

        public String extractEmailFromRefreshToken(String token) {
            Claims claims = Jwts.parserBuilder()
                   .setSigningKey(refreshSecret.getBytes())
                    .build()
                   .parseClaimsJws(token)
                   .getBody();
           return claims.getSubject();
        }

    }

