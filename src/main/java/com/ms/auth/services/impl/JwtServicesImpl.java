package com.ms.auth.services.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ms.auth.config.jwt.JwtProperties;
import com.ms.auth.models.CustomerPrincipal;
import com.ms.auth.services.JwtServices;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtServicesImpl implements JwtServices {

    private final JwtProperties jwtProperties;

    @Override
    public String issue(String id, String email, List<String> roles) {
        try {
            final Date now = new Date();
            return JWT
                    .create()
                    .withSubject(id)
                    .withIssuedAt(now)
                    .withExpiresAt(new Date(now.getTime() + 360000000))
                    .withClaim("email", email)
                    .withClaim("authorities", roles)
                    .sign(Algorithm.HMAC256(jwtProperties.getSecretKey()));
        } catch (Exception e) {
            // Tratar a exceção aqui ou relançá-la se necessário
            throw new RuntimeException("Erro ao emitir o token JWT", e);
        }
    }

    @Override
    public DecodedJWT decode(String token) {
        try {
            return JWT
                    .require(Algorithm.HMAC256(jwtProperties.getSecretKey()))
                    .build()
                    .verify(token);
        } catch (JWTVerificationException e) {
            // Tratar a exceção aqui
            throw new RuntimeException("Erro ao decodificar o token JWT: " + e.getMessage(), e);
        }
    }

    @Override
    public CustomerPrincipal convert(DecodedJWT jwt) {
        try {
            return CustomerPrincipal.builder()
                    .id(jwt.getId())
                    .email(jwt.getClaim("email").asString())
                    .authorities(extractAuthoritiesFromClaim(jwt))
                    .build();
        } catch (Exception e) {
            // Tratar a exceção aqui ou relançá-la se necessário
            throw new RuntimeException("Erro ao converter o token JWT para CustomerPrincipal", e);
        }
    }

    @Override
    public List<SimpleGrantedAuthority> extractAuthoritiesFromClaim(DecodedJWT jwt) {
        var claim = jwt.getClaim("authorities");
        if (claim.isNull() || claim.isMissing()) {
            return List.of();
        }
        try {
            return claim.asList(SimpleGrantedAuthority.class);
        } catch (Exception e) {
            // Tratar a exceção aqui ou relançá-la se necessário
            throw new RuntimeException("Erro ao extrair as autoridades do token JWT", e);
        }
    }
}
