package com.ms.auth.controllers;

import com.ms.auth.dtos.CustomerDto;
import com.ms.auth.dtos.LoginRequest;
import com.ms.auth.dtos.LoginResponse;
import com.ms.auth.dtos.RegistrationResponse;
import com.ms.auth.exceptions.DuplicateResourceException;
import com.ms.auth.exceptions.RequestValidationException;
import com.ms.auth.models.CustomerModel;
import com.ms.auth.services.impl.AuthServicesImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthServicesImpl authServices;

    public AuthController(AuthServicesImpl authServices) {
        this.authServices = authServices;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginRequest(@RequestBody LoginRequest request) {
        return this.authServices.loginRequest(request);
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registrationRequest(@RequestBody CustomerDto customerDto) {
        try {
            CustomerModel customerModel = customerDto.convertToCustomerModel();
            authServices.registrationRequest(customerModel);
            return ResponseEntity.ok().body(
                    RegistrationResponse.builder()
                            .status(HttpStatus.CREATED)
                            .message("Successfully registered customer!")
                            .build());

        } catch (RequestValidationException exception) {
            log.error("Error on create customer, error: {} ", exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        } catch (DuplicateResourceException exception) {
            log.error("Error on create customer, error: {} ", exception.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
        }
    }
}