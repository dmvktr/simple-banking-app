package com.assignment.bankingapp.controller;


import com.assignment.bankingapp.service.AccountService;
import com.assignment.bankingapp.service.CustomerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    AccountService accountService;
    CustomerService customerService;

    public CustomerController(AccountService accountService, CustomerService customerService) {
        this.accountService = accountService;
        this.customerService = customerService;
    }
}
