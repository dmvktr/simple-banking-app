package com.assignment.bankingapp.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerCreationRequest {
    private String assignedCustomerCode;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private Address address;
}
