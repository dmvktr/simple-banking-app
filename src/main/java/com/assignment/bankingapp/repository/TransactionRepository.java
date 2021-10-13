package com.assignment.bankingapp.repository;

import com.assignment.bankingapp.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;



public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
