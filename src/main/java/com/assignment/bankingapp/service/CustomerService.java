package com.assignment.bankingapp.service;

import com.assignment.bankingapp.entity.Customer;

import java.util.List;

public interface CustomerService {
    Customer createNewUser();
    Customer findByUsername();
    Customer findByEmailAddress();
    void freezeUser();
    List<Customer> getAllUsers();
    void saveUser(Customer customer);
}
