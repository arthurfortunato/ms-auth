package com.ms.auth.dtos;

import com.ms.auth.models.CustomerModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
public class CustomerDto {

    String role;
    String name;
    String email;
    String password;

    public CustomerModel convertToCustomerModel() {
        var customerModel = new CustomerModel();
        BeanUtils.copyProperties(this, customerModel);
        return customerModel;
    }
}
