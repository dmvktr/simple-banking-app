package com.assignment.bankingapp.service;

import com.assignment.bankingapp.entity.Account;
import com.assignment.bankingapp.entity.AccountType;
import com.assignment.bankingapp.entity.FundTransferRequest;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

public interface AccountService {
    Account createAndSaveNewAccount(Long userId, AccountType type, Currency currency);
    BigDecimal getAccountBalance(Long accountId);
    void transferFunds(BigDecimal amount, String sourceAccountNumber, String targetAccountNumber);
    void freezeAccount(Long accountId);
    void reactivateAccount(Long accountId);
    void withdraw(String accountNumber, BigDecimal withdrawAmount);
    void deposit(String accountNumber, BigDecimal depositAmount);
    boolean hasValidTransferDetails(FundTransferRequest transferRequest);
    Account findAccountByAccountNumber(String accountNumber);
    List<Account> findAccountsByCustomerId(Long id);
}
