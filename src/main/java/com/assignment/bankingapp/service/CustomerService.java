package com.assignment.bankingapp.service;

import com.assignment.bankingapp.entity.Customer;
import com.assignment.bankingapp.entity.CustomerCreationRequest;

import java.util.List;

public interface CustomerService {
    Customer newCustomer(CustomerCreationRequest customerCreationRequest);
    Customer findById(Long customerId);
    Customer findByEmailAddress(String email);
    List<Customer> getAllCustomers();
    void saveCustomer(Customer customer);
    void deleteAll();
}
