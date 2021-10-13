package com.assignment.bankingapp.repository;

import com.assignment.bankingapp.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findTransactionsByAccountId(Long accountId);
}
