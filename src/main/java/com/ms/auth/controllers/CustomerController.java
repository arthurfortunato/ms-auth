package com.ms.auth.controllers;

import com.ms.auth.dtos.CustomerDto;
import com.ms.auth.models.CustomerModel;
import com.ms.auth.models.CustomerPrincipal;
import com.ms.auth.repositories.CustomerRepository;
import com.ms.auth.services.CustomerServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerServices customerServices;
    private final CustomerRepository customerRepository;

    public CustomerController(CustomerServices customerServices, CustomerRepository customerRepository) {
        this.customerServices = customerServices;
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public ResponseEntity<List<CustomerModel>> getAllCustomers() {
        return ResponseEntity.status(HttpStatus.OK).body(customerServices.findAll());
    }

    @GetMapping("/{email}")
    public ResponseEntity<Object> getCustomerById(@PathVariable(value = "email") String email) {
        Optional<CustomerModel> customerModelOptional = customerServices.findByEmail(email);
        return customerModelOptional.<ResponseEntity<Object>>map(customerModel
                -> ResponseEntity.status(HttpStatus.OK).body(customerModel)).orElseGet(()
                -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found."));
    }

    @GetMapping("/profile")
    public ResponseEntity<CustomerModel> getCustomerDetails() {
        var principal = (CustomerPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var customer = this.customerRepository.findCustomerByEmail(principal.getEmail());
        return customer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable String id) {
        return this.customerServices.deleteCustomer(id);
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
