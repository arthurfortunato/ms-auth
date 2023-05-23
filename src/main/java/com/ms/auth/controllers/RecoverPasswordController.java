package com.ms.auth.controllers;

import com.ms.auth.dtos.CustomerDto;
import com.ms.auth.models.CustomerModel;
import com.ms.auth.repositories.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/recover")
public class RecoverPasswordController {

    private final CustomerRepository customerRepository;

    public RecoverPasswordController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @PutMapping("/{email}")
    public ResponseEntity<Object> updatePassword(@PathVariable("email") String email,
                                                 @RequestBody CustomerDto request) {
        try {
            CustomerModel customerModel = customerRepository.findByEmail(email).orElse(null);
            if (customerModel == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
            }

            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            customerModel.setPassword(encodedPassword);
            customerRepository.save(customerModel);

            return ResponseEntity.ok().body("Password successfully updated!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the user's password.");
        }
    }
}
