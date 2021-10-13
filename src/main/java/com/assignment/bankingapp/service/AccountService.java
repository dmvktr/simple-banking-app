package com.assignment.bankingapp.service;

import com.assignment.bankingapp.entity.Account;
import com.assignment.bankingapp.entity.AccountType;

import java.math.BigDecimal;
import java.util.Currency;

public interface AccountService { 
    Account createNewAccount(Long userId, AccountType type, Currency currency);
    BigDecimal getAccountBalance(Long accountId);
    void transferFunds(BigDecimal amount, int sourceAccountNumber, int targetAccountNumber);
    void freezeAccount(Long accountId);
    void reactivateAccount(Long accountId);
    void withdraw(Long accountId, BigDecimal amount);
    void deposit(Long accountId, BigDecimal amount);
}
