package com.assignment.bankingapp.controller;

import com.assignment.bankingapp.entity.*;
import com.assignment.bankingapp.notification.Notification;
import com.assignment.bankingapp.service.AccountService;
import com.assignment.bankingapp.service.CustomerService;
import com.assignment.bankingapp.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(value = "classpath:application.properties")
@AutoConfigureMockMvc
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        Address budapest = Address.builder().
            city("Budapest")
            .country("Hungary")
            .zipcode("1056")
            .street("Main street")
            .houseNumber("11")
            .build();

        Address london = Address.builder().
            city("London")
            .country("England")
            .zipcode("SW1P 2JJ")
            .street("Abbey Orchard Street")
            .houseNumber("2")
            .build();

        Customer matthew = Customer.builder()
            .email("matthew@example.com")
            .assignedCustomerCode("774464489")
            .password(passwordEncoder.encode("hello world!"))
            .phoneNumber("+462077477777")
            .createdAt(LocalDate.now())
            .firstName("Matthew")
            .lastName("Baker")
            .address(london)
            .build();

        Customer john = Customer.builder()
            .email("john@me.com")
            .assignedCustomerCode("774464489")
            .password(passwordEncoder.encode("here's johnnyX2"))
            .phoneNumber("+36204456845")
            .createdAt(LocalDate.now())
            .firstName("John")
            .lastName("Reed")
            .address(budapest)
            .build();

        customerService.saveCustomer(matthew);
        customerService.saveCustomer(john);
        Account matthewDeposit = accountService.createAndSaveNewAccount(1L, AccountType.DEPOSIT, Currency.getInstance(
            "HUF"));
        accountService.createAndSaveNewAccount(3L, AccountType.SAVINGS, Currency.getInstance(
            "HUF"));
        accountService.createAndSaveNewAccount(3L, AccountType.SAVINGS, Currency.getInstance(
            "HUF"));
        accountService.deposit(matthewDeposit.getAccountNumber(), BigDecimal.valueOf(500000));
    }

    @AfterEach
    void teardown() {
        accountService.deleteAll();
        transactionService.deleteAll();
    }

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
        Account newAccount = customerAccounts.get(1);
        newAccount.setBalance(newAccount.getBalance().stripTrailingZeros());

        ResponseObject expectedResponseBody =
            new ResponseObject(Notification.ACCOUNT_CREATION_SUCCESS.message(), 200, newAccount);
        String actualResponseBody = result.getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedResponseBody), actualResponseBody);
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
        Account account = accountService.findAccountsByCustomerId(3L).get(0);
        BigDecimal balance = account.getBalance();
        MvcResult result =
            mockMvc.perform(get("/account/{accountId}/balance", account.getId())
                    .contentType("application/json"))
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
        Account account = accountService.findAccountsByCustomerId(1L).get(0);
        FundRequest fundRequest = new FundRequest(account.getAccountNumber(), BigDecimal.valueOf(50000));
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
        Account account = accountService.findAccountsByCustomerId(1L).get(0);
        FundRequest fundRequest = new FundRequest(account.getAccountNumber(), BigDecimal.valueOf(500000000));
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
        Account account = accountService.findAccountsByCustomerId(1L).get(0);
        BigDecimal initialBalance = account.getBalance();
        FundRequest fundRequest = new FundRequest(account.getAccountNumber(), BigDecimal.valueOf(3333));
        mockMvc.perform(post("/account/withdraw")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(fundRequest)))
            .andDo(print())
            .andExpect(status().isOk());

        BigDecimal balanceAfterWithdraw = accountService.getAccountBalance(account.getId());
        BigDecimal expected = initialBalance.subtract(fundRequest.getAmount());
        assertEquals(expected, balanceAfterWithdraw);
    }

    @Test
    void deposit_returnsBadRequest_whenAmountIsLessThanOrEqualToZero() throws Exception {
        Account account = accountService.findAccountsByCustomerId(1L).get(0);
        FundRequest fundRequest = new FundRequest(account.getAccountNumber(), BigDecimal.ZERO);
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
        Account account = accountService.findAccountsByCustomerId(3L).get(1);
        FundRequest fundRequest = new FundRequest(account.getAccountNumber(), BigDecimal.valueOf(50000));
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
        Account account = accountService.findAccountsByCustomerId(3L).get(0);
        FundRequest fundRequest = new FundRequest(account.getAccountNumber(), BigDecimal.valueOf(50000));
        BigDecimal initialBalance = accountService.getAccountBalance(account.getId());
        mockMvc.perform(post("/account/deposit")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(fundRequest)))
            .andDo(print())
            .andExpect(status().isOk());

        BigDecimal afterDeposit = accountService.getAccountBalance(account.getId());
        BigDecimal expected = initialBalance.add(fundRequest.getAmount());
        assertEquals(expected, afterDeposit);
    }

    @Test
    void deposit_transactionIsPersisted_whenDepositSuccessful() throws Exception {
        Account account = accountService.findAccountsByCustomerId(3L).get(0);
        FundRequest fundRequest = new FundRequest(account.getAccountNumber(), BigDecimal.valueOf(50000));
        int expected = transactionService.findAllTransactionsOfAccountById(account.getId()).size() + 1;
        mockMvc.perform(post("/account/deposit")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(fundRequest)))
            .andDo(print())
            .andExpect(status().isOk());
        int actual = transactionService.findAllTransactionsOfAccountById(account.getId()).size();
        assertEquals(expected, actual);
    }

    @Test
    void transfer_returnsBadRequest_whenSourceAndTargetAccountNumberMatches() throws Exception {
        Account sourceAccount = accountService.findAccountsByCustomerId(1L).get(0);
//        Account targetAccount = accountService.findAccountsByCustomerId(3L).get(1);
        FundTransferRequest transferRequest =
            new FundTransferRequest(sourceAccount.getAccountNumber(), BigDecimal.valueOf(50000),
                sourceAccount.getAccountNumber(), "Matthew Baker", null);
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
        Account sourceAccount = accountService.findAccountsByCustomerId(1L).get(0);
        Account targetAccount = accountService.findAccountsByCustomerId(3L).get(1);
        FundTransferRequest transferRequest =
            new FundTransferRequest(sourceAccount.getAccountNumber(), BigDecimal.valueOf(50000),
                targetAccount.getAccountNumber(), "Frederik Smith", null);
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
        Account account = accountService.findAccountsByCustomerId(3L).get(1);
        FundTransferRequest transferRequest =
            new FundTransferRequest(account.getAccountNumber(), BigDecimal.valueOf(50000), null,
                "Matthew Baker", null);

        mockMvc.perform(post("/account/transfer")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(transferRequest)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    void transfer_returnsNotFound_whenAccountIsNotFoundById() throws Exception {
        Account account = accountService.findAccountsByCustomerId(3L).get(1);
        FundTransferRequest transferRequest =
            new FundTransferRequest(account.getAccountNumber(), BigDecimal.valueOf(50000), "11100033",
                "Matthew Baker", null);

        mockMvc.perform(post("/account/transfer")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(transferRequest)))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void transfer_returnsJsonResponseBodyWithStatusOk_whenInputIsValidAndTransferSuccessful() throws Exception {
        Account sourceAccount = accountService.findAccountsByCustomerId(1L).get(0);
        Account targetAccount = accountService.findAccountsByCustomerId(3L).get(1);
        FundTransferRequest transferRequest =
            new FundTransferRequest(sourceAccount.getAccountNumber(), BigDecimal.valueOf(10000),
                targetAccount.getAccountNumber(), "John Reed", null);

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
        Account sourceAccount = accountService.findAccountsByCustomerId(1L).get(0);
        Account targetAccount = accountService.findAccountsByCustomerId(3L).get(1);
        BigDecimal initialSourceAccountBalance = sourceAccount.getBalance();
        FundTransferRequest transferRequest =
            new FundTransferRequest(sourceAccount.getAccountNumber(), BigDecimal.valueOf(50000),
                targetAccount.getAccountNumber(), "John Reed", null);

        mockMvc.perform(post("/account/transfer")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(transferRequest)))
            .andDo(print())
            .andExpect(status().isOk());

        BigDecimal actualTargetAccountBalance =
            accountService.findAccountByAccountNumber(targetAccount.getAccountNumber()).getBalance().stripTrailingZeros();
        BigDecimal expectedTargetAccountBalance = transferRequest.getAmount().stripTrailingZeros();
        BigDecimal expectedSourceAccountBalance = initialSourceAccountBalance.subtract(transferRequest.getAmount())
            .stripTrailingZeros();
        BigDecimal actualSourceAccountBalance = accountService.getAccountBalance(sourceAccount.getId())
            .stripTrailingZeros();

        assertEquals(expectedSourceAccountBalance, actualSourceAccountBalance);
        assertEquals(expectedTargetAccountBalance, actualTargetAccountBalance);
    }

    @Test
    void transfer_returnsBadRequest_whenSourceAccountHasNotEnoughFunds() throws Exception {
        Account sourceAccount = accountService.findAccountsByCustomerId(1L).get(0);
        Account targetAccount = accountService.findAccountsByCustomerId(3L).get(1);
        FundTransferRequest transferRequest =
            new FundTransferRequest(sourceAccount.getAccountNumber(), BigDecimal.valueOf(500000000),
                targetAccount.getAccountNumber(), "John Reed", null);

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

    @Test
    void transfer_transactionsArePersistedForSourceAndRecipient_whenTransferSuccessful() throws Exception {
        Account sourceAccount = accountService.findAccountsByCustomerId(1L).get(0);
        Account recipient = accountService.findAccountsByCustomerId(3L).get(1);
        FundTransferRequest transferRequest =
            new FundTransferRequest(sourceAccount.getAccountNumber(), BigDecimal.valueOf(50000),
                recipient.getAccountNumber(), "John Reed", null);

        int recipientExpected = transactionService.findAllTransactionsOfAccountById(recipient.getId()).size() + 1;
        int sourceAccountExpectedCount =
            transactionService.findAllTransactionsOfAccountById(sourceAccount.getId()).size() + 1;

        mockMvc.perform(post("/account/transfer")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(transferRequest)))
            .andDo(print())
            .andExpect(status().isOk());
        int recipientActualCount = transactionService.findAllTransactionsOfAccountById(recipient.getId()).size();
        int sourceAccountActualCount =
            transactionService.findAllTransactionsOfAccountById(sourceAccount.getId()).size();

        assertEquals(recipientExpected, recipientActualCount);
        assertEquals(sourceAccountExpectedCount, sourceAccountActualCount);
    }
}