package com.assignment.bankingapp.repository;

import com.assignment.bankingapp.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findAccountByAccountNumber(String accountNumber);
    List<Account> findAccountsByCustomerId(Long Id);
}
