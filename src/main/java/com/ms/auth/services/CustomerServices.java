package com.ms.auth.services;

import com.ms.auth.models.CustomerModel;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface CustomerServices {

    ResponseEntity<String> deleteCustomer(String id);

    List<CustomerModel> findAll();

    Optional<CustomerModel> findByEmail(String email);
}
