package com.assignment.bankingapp.entity;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerCreationRequest {
    @NotNull
    private String assignedCustomerCode;
    @NotNull
    private String password;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String phoneNumber;
    @NotNull
    private String email;
    @NotNull
    private Address address;
}
