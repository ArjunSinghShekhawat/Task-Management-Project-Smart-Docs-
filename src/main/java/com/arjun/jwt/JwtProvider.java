package com.arjun.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Component
public class JwtProvider {

    private final SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    public String generateToken(Authentication authentication){

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = populateAuthorities(authorities);

        return Jwts.builder()
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime()+86400000))
                .claim("email",authentication.getName())
                .claim("authorities",role)
                .signWith(key)
                .compact();
    }
    public String getEmailFromJwtToken(String jwt) {
        jwt = jwt.substring(7);
        Claims claims = Jwts.parser().verifyWith(key).build().parseClaimsJws(jwt).getPayload();
        return String.valueOf(claims.get("email"));
    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> collection) {
        Set<String> auths = new HashSet<>();

        for (GrantedAuthority authority : collection) {
            auths.add(authority.getAuthority());
        }
        return String.join(",", auths);
    }
}
