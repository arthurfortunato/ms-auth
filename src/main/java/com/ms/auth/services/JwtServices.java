package com.ms.auth.services;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.ms.auth.models.CustomerPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public interface JwtServices {
    String issue(String id, String email, List<String> roles);

    DecodedJWT decode(String token);

    CustomerPrincipal convert(DecodedJWT jwt);

    List<SimpleGrantedAuthority> extractAuthoritiesFromClaim(DecodedJWT jwt);
}
