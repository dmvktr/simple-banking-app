package com.assignment.bankingapp;

import com.assignment.bankingapp.controller.AccountController;
import com.assignment.bankingapp.controller.CustomerController;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BankingAppApplicationTests {

    @Autowired
    CustomerController customerController;
    @Autowired
    AccountController accountController;

    @Test
    void contextLoads() {
        assertNotNull(customerController);
        assertNotNull(accountController);
    }

}
