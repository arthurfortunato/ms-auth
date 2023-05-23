package com.ms.auth.services.impl;

import com.ms.auth.exceptions.ResourceNotFoundException;
import com.ms.auth.models.CustomerModel;
import com.ms.auth.repositories.CustomerRepository;
import com.ms.auth.services.CustomerServices;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServicesImpl implements CustomerServices {

    private final CustomerRepository customerRepository;

    public CustomerServicesImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public ResponseEntity<String> deleteCustomer(String id) {
        var customer = this.customerRepository.findById(id);
        if (customer.isEmpty())
            throw new ResourceNotFoundException("Failed in deleting. CustomerModel with id " + id + " does not exists.");
        this.customerRepository.deleteById(id);
        return ResponseEntity.ok().body("CustomerModel with id " + id + " has been deleted.");
    }

    @Override
    public List<CustomerModel> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<CustomerModel> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }
}
