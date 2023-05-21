package com.ms.auth.repositories;

import com.ms.auth.models.CustomerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerModel, String> {

    Optional<CustomerModel> findCustomerByEmail(String username);
}
