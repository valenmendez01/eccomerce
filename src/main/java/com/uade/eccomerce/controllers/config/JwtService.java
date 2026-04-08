package com.uade.eccomerce.controllers.config;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

@Service
public class JwtService {
    @Value("${application.security.jwt.secretKey}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    // Inyecta el secretKey y la expiración desde el application.properties para generar el token en buildToken y validar el token en isTokenValid
    public String generateToken(
            UserDetails userDetails) {
        return buildToken(userDetails, jwtExpiration);
    }

    // Genera el token con los detalles del usuario, la fecha de emisión, la fecha de expiración y la firma con el secretKey
    // aca se configura que datos se quieren incluir en el token [HACER]
    private String buildToken(
            UserDetails userDetails,
            long expiration) {
        return Jwts
                .builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey())
                .compact();
    }

    // Valida el token comparando el nombre del usuario del token con el nombre del usuario de los detalles del usuario y verificando que el token no haya expirado
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractClaim(token, Claims::getSubject);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Verifica si el token ha expirado comparando la fecha de expiración del token con la fecha actual
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    // El resto de los métodos son para extraer el nombre del usuario y los claims del token (los datos del token) utilizando el secretKey para verificar la firma del token

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey() {
        SecretKey secretKeySpec = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return secretKeySpec;
    }
}
