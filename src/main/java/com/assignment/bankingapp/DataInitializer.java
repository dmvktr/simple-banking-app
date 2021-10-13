package com.assignment.bankingapp;

import com.assignment.bankingapp.entity.*;
import com.assignment.bankingapp.service.AccountService;
import com.assignment.bankingapp.service.CustomerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

@Component
@Profile("prod")
public class DataInitializer implements CommandLineRunner {

    CustomerService customerService;
    AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(CustomerService customerService, AccountService accountService) {
        this.customerService = customerService;
        this.accountService = accountService;
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
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
            .assignedCustomerCode("773454489")
            .password(passwordEncoder.encode("hello world!"))
            .phoneNumber("+462077477777")
            .createdAt(LocalDate.now())
            .firstName("John")
            .lastName("Smith")
            .address(address)
            .build();

        customerService.saveCustomer(customer);
        Account account = accountService.createAndSaveNewAccount(1L, AccountType.DEPOSIT, Currency.getInstance("HUF"));
        accountService.deposit(account.getAccountNumber(), BigDecimal.valueOf(500000));
    }
}