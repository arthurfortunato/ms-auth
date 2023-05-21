package com.ms.auth.services.impl;

import com.ms.auth.exceptions.ResourceNotFoundException;
import com.ms.auth.models.CustomerPrincipal;
import com.ms.auth.repositories.CustomerRepository;
import com.ms.auth.services.CustomerUserDetailServices;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerUserDetailServicesImpl implements CustomerUserDetailServices {

    private final CustomerRepository customerRepository;

    public CustomerUserDetailServicesImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var customer = this.customerRepository.findCustomerByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("CustomerModel with email " + username + " does not exists."));
        return CustomerPrincipal.builder()
                .id(customer.getId())
                .email(customer.getEmail())
                .authorities(List.of(new SimpleGrantedAuthority(customer.getRole())))
                .password(customer.getPassword())
                .build();
    }
}
