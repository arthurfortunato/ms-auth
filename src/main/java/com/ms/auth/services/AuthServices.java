package com.ms.auth.services;

import com.ms.auth.dtos.LoginRequest;
import com.ms.auth.dtos.LoginResponse;
import com.ms.auth.models.CustomerModel;
import org.springframework.http.ResponseEntity;

public interface AuthServices {
    ResponseEntity<LoginResponse> loginRequest(LoginRequest request);

    CustomerModel registrationRequest(CustomerModel userModel);

}

