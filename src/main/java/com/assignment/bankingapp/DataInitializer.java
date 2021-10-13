package com.assignment.bankingapp;

import com.assignment.bankingapp.entity.*;
import com.assignment.bankingapp.service.AccountService;
import com.assignment.bankingapp.service.CustomerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Currency;

@Component
@Profile("prod")
public class DataInitializer implements CommandLineRunner {

    CustomerService customerService;
    AccountService accountService;

    public DataInitializer(CustomerService customerService, AccountService accountService) {
        this.customerService = customerService;
        this.accountService = accountService;
    }

    @Override
    public void run(String... args) {
        Address address = Address.builder().
            city("Budapest")
            .country("Hungary")
            .zipcode(1056)
            .street("Main street")
            .houseNumber(11)
            .build();

        Customer customer = Customer.builder()
            .email("customer@example.com")
            .firstName("John")
            .lastName("Smith")
            .address(address)
            .build();

        customerService.saveCustomer(customer);
        Account account = accountService.createAndSaveNewAccount(1L, AccountType.DEPOSIT, Currency.getInstance("HUF"));
        accountService.deposit(account.getAccountNumber(), BigDecimal.valueOf(500000));
    }
}