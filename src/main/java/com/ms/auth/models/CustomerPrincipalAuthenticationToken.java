package com.ms.auth.models;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class CustomerPrincipalAuthenticationToken extends AbstractAuthenticationToken {

    private final CustomerPrincipal customerPrincipal;

    public CustomerPrincipalAuthenticationToken(CustomerPrincipal customerPrincipal) {
        super(customerPrincipal.getAuthorities());
        this.customerPrincipal = customerPrincipal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.customerPrincipal;
    }
}
