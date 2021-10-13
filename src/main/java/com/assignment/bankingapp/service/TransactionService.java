package com.assignment.bankingapp.service;

import com.assignment.bankingapp.entity.Account;
import com.assignment.bankingapp.entity.Transaction;
import com.assignment.bankingapp.entity.TransactionType;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {
    void createAndSaveNewTransaction(BigDecimal amount, String note, TransactionType type, Account source);
    void createTransferTransaction(BigDecimal amount, String note, TransactionType type, Account source, Account recipient);
    List<Transaction> listAllTransactions();
    List<Transaction> findAllTransactionsOfAccountById(Long accountId);
}
