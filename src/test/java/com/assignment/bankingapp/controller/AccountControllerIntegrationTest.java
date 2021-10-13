package com.assignment.bankingapp.controller;

import com.assignment.bankingapp.entity.*;
import com.assignment.bankingapp.notification.Notification;
import com.assignment.bankingapp.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "classpath:application.properties")
@AutoConfigureMockMvc
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void newAccount_returnsNotFound_whenCustomerIsNotFound() throws Exception {
        AccountCreationRequest newAccountRequest = new AccountCreationRequest(Currency.getInstance("HUF"),
            AccountType.DEPOSIT);
        MvcResult result = mockMvc.perform(post("/account/{userId}/newAccount", 13334L)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newAccountRequest)))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andReturn();

        ResponseObject expectedResponseBody =
            new ResponseObject(Notification.CUSTOMER_NOT_FOUND.message(), 404, null);
        String actualResponseBody = result.getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedResponseBody), actualResponseBody);
    }

    @Test
    void newAccount_returnsReturnsJsonResponseBodyWithStatusOkAndSuccessfullyPersisted_whenSuccessful() throws Exception {
        AccountCreationRequest newAccountRequest = new AccountCreationRequest(Currency.getInstance("HUF"),
            AccountType.DEPOSIT);
        MvcResult result = mockMvc.perform(post("/account/{userId}/newAccount", 1L)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newAccountRequest)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        List<Account> customerAccounts = accountService.findAccountsByCustomerId(1L);
        Account newAccount = null;
        if (customerAccounts.size() > 0) {
            newAccount = customerAccounts.get(customerAccounts.size() - 1);
            newAccount.setBalance(newAccount.getBalance().stripTrailingZeros());
        }

        ResponseObject expectedResponseBody =
            new ResponseObject(Notification.ACCOUNT_CREATION_SUCCESS.message(), 200, newAccount);
        String actualResponseBody = result.getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedResponseBody), actualResponseBody);
    }

    @Test
    void getBalance_returnsNotFound_whenAccountIsNotFoundById() throws Exception {
        mockMvc.perform(get("/account/{accountId}/balance", 232L).contentType("application/json"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void getBalance_returnsJsonResponseBodyWithStatusNotFound_whenResourceIsNotFound() throws Exception {
        MvcResult result =
            mockMvc.perform(get("/account/{accountId}/balance", 232L).contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        ResponseObject expectedResponseBody =
            new ResponseObject(Notification.ACCOUNT_NOT_FOUND.message(), 404, null);
        String actualResponseBody = result.getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedResponseBody), actualResponseBody);
    }

    @Test
    void getBalance_returnsJsonResponseBodyWithStatusOk_whenAccountIsFound() throws Exception {
        BigDecimal balance = accountService.getAccountBalance(3L);
        MvcResult result =
            mockMvc.perform(get("/account/{accountId}/balance", 3L).contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        ResponseObject expectedResponseBody =
            new ResponseObject(Notification.BALANCE_CHECK_SUCCESS.message(), 200, balance);
        String actualResponseBody = result.getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedResponseBody), actualResponseBody);
    }
}