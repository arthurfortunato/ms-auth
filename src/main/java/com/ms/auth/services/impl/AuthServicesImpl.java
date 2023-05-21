package com.ms.auth.services.impl;

import com.ms.auth.dtos.LoginRequest;
import com.ms.auth.dtos.LoginResponse;
import com.ms.auth.exceptions.DuplicateResourceException;
import com.ms.auth.exceptions.RequestValidationException;
import com.ms.auth.models.CustomerModel;
import com.ms.auth.models.CustomerPrincipal;
import com.ms.auth.repositories.CustomerRepository;
import com.ms.auth.services.AuthServices;
import com.ms.auth.services.JwtServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AuthServicesImpl implements AuthServices {
    private final CustomerRepository customerRepository;
    private final JwtServices jwtServices;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthServicesImpl(CustomerRepository customerRepository,
                            JwtServices jwtServices,
                            PasswordEncoder passwordEncoder,
                            AuthenticationManager authenticationManager) {
        this.customerRepository = customerRepository;
        this.jwtServices = jwtServices;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public ResponseEntity<LoginResponse> loginRequest(LoginRequest request) {
        log.info("Starting customer login");

        var customer = this.customerRepository.findCustomerByEmail(request.getEmail());

        if (customer.isEmpty()) {
            log.info("Incorrect login! Please try again.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    LoginResponse.builder()
                            .status(HttpStatus.UNAUTHORIZED)
                            .message("Incorrect login! Please try again.")
                            .build()
            );
        }

        var entity = customer.get();
        if (!passwordEncoder.matches(request.getPassword(), entity.getPassword())) {
            log.info("Incorrect login! Please try again.");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    LoginResponse.builder()
                            .status(HttpStatus.UNAUTHORIZED)
                            .message("Incorrect login! Please try again.")
                            .build()
            );
        }

        var authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        var principal = (CustomerPrincipal) authentication.getPrincipal();
        var roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        var accessToken = this.jwtServices.issue(principal.getId(), principal.getUsername(), roles);

        log.info("Successfully login customer!");

        return ResponseEntity.ok().body(
                LoginResponse.builder()
                        .status(HttpStatus.OK)
                        .accessToken(accessToken)
                        .message("You have successfully logged in!")
                        .build()
        );
    }

    @Override
    public CustomerModel registrationRequest(CustomerModel customerModel) {
        log.info("Starting customer registration");
        if (customerModel.getName().isEmpty()
                || customerModel.getEmail().isEmpty()
                || customerModel.getPassword().isEmpty()) {
            throw new RequestValidationException("Invalid registration, please fill in the fields correctly.");
        }

        Optional<CustomerModel> existingUser = customerRepository.findCustomerByEmail(customerModel.getEmail());
        if (existingUser.isPresent()) {
            throw new DuplicateResourceException("Email already taken");
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(customerModel.getPassword());
        customerModel.setPassword(encodedPassword);
        customerModel.setRole("ROLE_USER");

        log.info("Successfully registered customer!");
        return customerRepository.save(customerModel);
    }
}
