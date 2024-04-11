package com.springauth.auth.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "84fe6d7bddfaa2becd1442b454853786ea7aed767c7017ec";
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Claims extractAllClaims(String token){
        System.out.println("All claims:::" + Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build().parseClaimsJws(token)
                .getBody());

        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build().parseClaimsJws(token)
                .getBody();
    }

    private <T>T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        System.out.println("claimsResolver.apply(claims) ::" + claimsResolver.apply(claims));
        return claimsResolver.apply(claims);
    }

    public String generateToken( UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }
    public String generateToken(Map<String, Object> extractClaims, UserDetails userDetails){
        JwtBuilder jwtBuilder = Jwts.builder();
        
        JwtBuilder setJwtClaims = jwtBuilder.setClaims(extractClaims);

        JwtBuilder setJwtSubject =  setJwtClaims.setSubject(userDetails.getUsername());
        
        JwtBuilder setJwtIssuedAt = setJwtSubject.setIssuedAt(new Date(System.currentTimeMillis()));

        JwtBuilder setJwtExpiration = setJwtIssuedAt.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24));
        
        JwtBuilder sign = setJwtExpiration.signWith(getSignInKey(), SignatureAlgorithm.HS256);
        
        return sign.compact();
    }
    
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals((userDetails.getUsername())) && !isTokenExired(token));
    }

    private boolean isTokenExired(String token) {
        return  extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
