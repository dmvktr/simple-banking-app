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

    @Test
    void withdraw_returnsBadRequest_whenAmountIsLessThanOrEqualToZero() throws Exception {
        FundRequest fundRequest = new FundRequest("10000000", BigDecimal.ZERO);
        mockMvc.perform(post("/account/withdraw")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(fundRequest)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    void withdraw_returnsBadRequest_whenRequestBodyIsMissing() throws Exception {
        mockMvc.perform(post("/account/withdraw")
                .contentType("application/json"))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    void withdraw_returnsBadRequest_whenRequestBodyObjectFieldIsNull() throws Exception {
        FundRequest fundRequest = new FundRequest(null, BigDecimal.valueOf(111));
        mockMvc.perform(post("/account/withdraw")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(fundRequest)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    void withdraw_returnsJsonResponseBodyWithStatusOk_whenAccountHasEnoughFund() throws Exception {
        FundRequest fundRequest = new FundRequest("10000000", BigDecimal.valueOf(50000));
        MvcResult result = mockMvc.perform(post("/account/withdraw")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(fundRequest)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        ResponseObject expectedResponseBody =
            new ResponseObject(Notification.WITHDRAW_SUCCESS.message(), 200, null);
        String actualResponseBody = result.getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedResponseBody), actualResponseBody);
    }

    @Test
    void withdraw_returnsJsonResponseBodyWithStatusNotFound_whenAccountDoesNotHaveEnoughFunds() throws Exception {
        FundRequest fundRequest = new FundRequest("10000000", BigDecimal.valueOf(500000000));
        MvcResult result = mockMvc.perform(post("/account/withdraw")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(fundRequest)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andReturn();

        ResponseObject expectedResponseBody =
            new ResponseObject(Notification.INSUFFICIENT_FUNDS.message(), 400, null);
        String actualResponseBody = result.getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedResponseBody), actualResponseBody);
    }

    @Test
    void withdraw_amountIsDeductedFromBalanceAndPersisted_whenAccountHasEnoughFund() throws Exception {
        BigDecimal initialBalance = accountService.getAccountBalance(3L);
        FundRequest fundRequest = new FundRequest("10000000", BigDecimal.valueOf(3333));
        mockMvc.perform(post("/account/withdraw")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(fundRequest)))
            .andDo(print())
            .andExpect(status().isOk());

        BigDecimal balanceAfterWithdraw = accountService.getAccountBalance(3L);
        BigDecimal expected = initialBalance.subtract(fundRequest.getAmount());
        assertEquals(expected, balanceAfterWithdraw);
    }

    @Test
    void deposit_returnsBadRequest_whenAmountIsLessThanOrEqualToZero() throws Exception {
        FundRequest fundRequest = new FundRequest("10000000", BigDecimal.ZERO);
        mockMvc.perform(post("/account/deposit")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(fundRequest)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    void deposit_returnsBadRequest_whenRequestBodyIsMissing() throws Exception {
        mockMvc.perform(post("/account/deposit")
                .contentType("application/json"))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    void deposit_returnsBadRequest_whenRequestBodyObjectFieldIsNull() throws Exception {
        FundRequest fundRequest = new FundRequest(null, BigDecimal.valueOf(111));
        mockMvc.perform(post("/account/deposit")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(fundRequest)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    void deposit_returnsJsonResponseBodyStatusOk_whenInputIsValid() throws Exception {
        FundRequest fundRequest = new FundRequest("10000000", BigDecimal.valueOf(50000));
        MvcResult result = mockMvc.perform(post("/account/deposit")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(fundRequest)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        ResponseObject expectedResponseBody =
            new ResponseObject(Notification.DEPOSIT_SUCCESS.message(), 200, null);
        String actualResponseBody = result.getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedResponseBody), actualResponseBody);
    }

    @Test
    void deposit_amountIsAddedToAndPersistedOnAccount_whenInputIsValid() throws Exception {
        FundRequest fundRequest = new FundRequest("10000000", BigDecimal.valueOf(50000));
        BigDecimal initialBalance = accountService.getAccountBalance(3L);
        mockMvc.perform(post("/account/deposit")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(fundRequest)))
            .andDo(print())
            .andExpect(status().isOk());

        BigDecimal afterDeposit = accountService.getAccountBalance(3L);
        BigDecimal expected = initialBalance.add(fundRequest.getAmount());
        assertEquals(expected, afterDeposit);
    }

    @Test
    void transfer_returnsBadRequest_whenSourceAndTargetAccountNumberMatches() throws Exception {
        FundTransferRequest transferRequest =
            new FundTransferRequest("10000000", BigDecimal.valueOf(50000), "10000000",
                "Matthew Baker", null);
        MvcResult result = mockMvc.perform(post("/account/transfer")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(transferRequest)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andReturn();

        ResponseObject expectedResponseBody =
            new ResponseObject(Notification.INVALID_TRANSFER_DETAILS.message(), 400, null);
        String actualResponseBody = result.getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedResponseBody), actualResponseBody);
    }

    @Test
    void transfer_returnsBadRequest_whenTargetAccountNameDoesNotMatch() throws Exception {
        Account targetAccount  = accountService.createNewAccount(1L, AccountType.DEPOSIT, Currency.getInstance("HUF"));
        FundTransferRequest transferRequest =
            new FundTransferRequest("10000000", BigDecimal.valueOf(50000), targetAccount.getAccountNumber(),
                "Matthew Baker", null);
        MvcResult result = mockMvc.perform(post("/account/transfer")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(transferRequest)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andReturn();

        ResponseObject expectedResponseBody =
            new ResponseObject(Notification.INVALID_TRANSFER_DETAILS.message(), 400, null);
        String actualResponseBody = result.getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedResponseBody), actualResponseBody);
    }

    @Test
    void transfer_returnsBadRequest_whenRequestBodyObjectFieldIsNull() throws Exception {
        FundTransferRequest transferRequest =
            new FundTransferRequest("10000000", BigDecimal.valueOf(50000), null,
                "Matthew Baker", null);

        mockMvc.perform(post("/account/transfer")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(transferRequest)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    void transfer_returnsNotFound_whenAccountIsNotFoundById() throws Exception {
        FundTransferRequest transferRequest =
            new FundTransferRequest("10000000", BigDecimal.valueOf(50000), "10000033",
                "Matthew Baker", null);

        mockMvc.perform(post("/account/transfer")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(transferRequest)))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void transfer_returnsJsonResponseBodyWithStatusOk_whenInputIsValidAndTransferSuccessful() throws Exception {
        Account targetAccount  = accountService.createNewAccount(1L, AccountType.DEPOSIT, Currency.getInstance("HUF"));
        FundTransferRequest transferRequest =
            new FundTransferRequest("10000000", BigDecimal.valueOf(10000), targetAccount.getAccountNumber(),
                "John Smith", null);

        MvcResult result = mockMvc.perform(post("/account/transfer")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(transferRequest)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        ResponseObject expectedResponseBody = new ResponseObject(Notification.TRANSFER_SUCCESS.message(), 200, null);
        String actualResponseBody = result.getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedResponseBody), actualResponseBody);
    }

    @Test
    void transfer_sourceAccountBalanceDeductedTargetAccountBalanceAddedAndPersisted_whenInputIsValid() throws Exception {
        Account targetAccount  =
            accountService.createNewAccount(1L, AccountType.DEPOSIT, Currency.getInstance("HUF"));
        BigDecimal initialSourceAccountBalance = accountService.getAccountBalance(3L);
        FundTransferRequest transferRequest =
            new FundTransferRequest("10000000", BigDecimal.valueOf(50000), targetAccount.getAccountNumber(),
                "John Smith", null);

        mockMvc.perform(post("/account/transfer")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(transferRequest)))
            .andDo(print())
            .andExpect(status().isOk());

        BigDecimal actualTargetAccountBalance =
            accountService.findAccountAccountNumber(targetAccount.getAccountNumber()).getBalance().stripTrailingZeros();
        BigDecimal expectedTargetAccountBalance = transferRequest.getAmount().stripTrailingZeros();
        BigDecimal expectedSourceAccountBalance = initialSourceAccountBalance.subtract(transferRequest.getAmount())
            .stripTrailingZeros();
        BigDecimal actualSourceAccountBalance = accountService.getAccountBalance(3L).stripTrailingZeros();

        assertEquals(expectedSourceAccountBalance, actualSourceAccountBalance);
        assertEquals(expectedTargetAccountBalance, actualTargetAccountBalance);
    }

    @Test
    void transfer_returnsBadRequest_whenSourceAccountHasNotEnoughFunds() throws Exception {
        Account targetAccount  =
            accountService.createNewAccount(1L, AccountType.DEPOSIT, Currency.getInstance("HUF"));
        FundTransferRequest transferRequest =
            new FundTransferRequest("10000000", BigDecimal.valueOf(500000000), targetAccount.getAccountNumber(),
                "John Smith", null);

        MvcResult result = mockMvc.perform(post("/account/transfer")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(transferRequest)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andReturn();

        ResponseObject expectedResponseBody =
            new ResponseObject(Notification.INSUFFICIENT_FUNDS.message(), 400, null);
        String actualResponseBody = result.getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedResponseBody), actualResponseBody);
    }
}