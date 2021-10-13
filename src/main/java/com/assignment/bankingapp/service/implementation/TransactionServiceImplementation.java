package com.assignment.bankingapp.service.implementation;

import com.assignment.bankingapp.entity.Account;
import com.assignment.bankingapp.entity.Transaction;
import com.assignment.bankingapp.entity.TransactionType;
import com.assignment.bankingapp.repository.AccountRepository;
import com.assignment.bankingapp.repository.TransactionRepository;
import com.assignment.bankingapp.service.TransactionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImplementation implements TransactionService {

    AccountRepository accountRepository;
    TransactionRepository transactionRepository;

    public TransactionServiceImplementation(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void createAndSaveNewTransaction(BigDecimal amount, String note, TransactionType type, Account sourceAccount) {
        Transaction transaction = Transaction.builder()
            .account(sourceAccount)
            .amount(amount)
            .time(LocalDateTime.now())
            .note(note)
            .type(type)
            .build();
        sourceAccount.getTransactions().add(transaction);
        transaction.setAccount(sourceAccount);
        transactionRepository.save(transaction);
        accountRepository.save(sourceAccount);
    }

    @Override
    public void createTransferTransaction(BigDecimal amount, String note, TransactionType type, Account sourceAccount,
                                          Account recipient) {
        createAndSaveNewTransaction(amount.negate(), note, type, sourceAccount);
        createAndSaveNewTransaction(amount, note, type, recipient);
    }

    @Override
    public List<Transaction> listAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public List<Transaction> findAllTransactionsOfAccountById(Long accountId) {
        return transactionRepository.findTransactionsByAccountId(accountId);
    }
}
