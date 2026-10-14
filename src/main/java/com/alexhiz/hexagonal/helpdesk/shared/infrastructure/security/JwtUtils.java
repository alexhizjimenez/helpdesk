package com.alexhiz.hexagonal.helpdesk.shared.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    @Value("${security.jwt.key.private}")
    private String privateKey;

    @Value("${security.jwt.auth.user}")
    private String userGenerator;

    public String createToken(Authentication authentication){
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);
            if (authentication == null || authentication.getPrincipal() == null) {
                throw new IllegalStateException("Authentication or principal is null");
            }
            String username = authentication.getPrincipal().toString();
            String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
            return JWT.create().withIssuer(this.userGenerator).withSubject(username)
                    .withClaim("authorities", authorities)
                    .withJWTId(UUID.randomUUID().toString())
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
                    .withNotBefore(new Date(System.currentTimeMillis()))
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new JWTCreationException("Error created token", exception.getCause());
        }
    }

    public DecodedJWT validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(this.userGenerator).build();
            return verifier.verify(token);
        }catch (JWTVerificationException exception){
            throw  new  JWTVerificationException("TOken invalid, not authorized");
        }
    }

    public String extractUsername(DecodedJWT decodedJWT){
        return decodedJWT.getSubject();
    }

    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName){
        return decodedJWT.getClaim(claimName);
    }

    public Map<String, Claim> returnAllClaims(DecodedJWT decodedJWT){
        return decodedJWT.getClaims();
    }

}
