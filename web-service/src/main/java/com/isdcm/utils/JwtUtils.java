/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.isdcm.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwtException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtils {
    // La clave secreta en bytes
    private static final byte[] SECRET =
        AppConfig.get("jwt.secret")
                 .getBytes(StandardCharsets.UTF_8);

    // 1 hora en milisegundos
    public static final long EXP_MS = 3_600_000L;

    /**
     * Genera un JWT firmado con HS512 y expiración a 1h
     */
    public static String generateToken(String subject) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + EXP_MS))
                // version 0.7.0: algoritmo primero, luego byte[] clave
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    /**
     * Valida firma y expiración. Devuelve true sólo si parsea OK.
     */
    public static boolean validate(String token) {
        try {
            Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Extrae el subject (p.ej. username) del JWT ya validado.
     */
    public static String getSubject(String token) {
        Claims claims = Jwts.parser()
                            .setSigningKey(SECRET)
                            .parseClaimsJws(token)
                            .getBody();
        return claims.getSubject();
    }
}