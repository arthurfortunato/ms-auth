package com.ms.auth.controllers;

import com.ms.auth.models.CustomerModel;
import com.ms.auth.models.CustomerPrincipal;
import com.ms.auth.repositories.CustomerRepository;
import com.ms.auth.services.CustomerServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCustomerById(@PathVariable(value = "id") String id) {
        Optional<CustomerModel> customerModelOptional = customerServices.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(customerModelOptional.get());
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
}
