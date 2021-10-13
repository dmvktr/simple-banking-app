package com.assignment.bankingapp.service.implementation;

import com.assignment.bankingapp.entity.*;
import com.assignment.bankingapp.exception.InvalidUserRequestException;
import com.assignment.bankingapp.notification.Notification;
import com.assignment.bankingapp.repository.AccountRepository;
import com.assignment.bankingapp.repository.CustomerRepository;
import com.assignment.bankingapp.service.AccountService;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

@Service
public class AccountServiceImplementation implements AccountService {

    AccountRepository accountRepository;
    CustomerRepository customerRepository;

    private static int lastAssignedAccountNumber = 10000000;

    public AccountServiceImplementation(AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    @Override
    public Account createNewAccount(Long userId, AccountType type, Currency currency) {
        String accountNumber = generateNewAccountNumber();
        Customer customer = customerRepository.findById(userId).orElseThrow(() ->
            new DataRetrievalFailureException(Notification.CUSTOMER_NOT_FOUND.message()));
        Account newAccount = Account.builder()
            .balance(BigDecimal.ZERO)
            .currency(currency).type(type)
            .customer(customer)
            .accountNumber(accountNumber)
            .build();
        customer.getAccounts().add(newAccount);
        newAccount.setCustomer(customer);
        customerRepository.save(customer);
        return accountRepository.save(newAccount);
    }

    public String generateNewAccountNumber() {
        return String.valueOf(lastAssignedAccountNumber++);
    }

    @Override
    public BigDecimal getAccountBalance(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() ->
            new DataRetrievalFailureException(Notification.ACCOUNT_NOT_FOUND.message()));
        return account.getBalance();
    }

    @Transactional
    @Override
    public void transferFunds(BigDecimal amount, String sourceAccountNumber, String targetAccountNumber) {
        try {
            Account sourceAccount = accountRepository.findAccountByAccountNumber(sourceAccountNumber).orElse(null);
            Account targetAccount = accountRepository.findAccountByAccountNumber(targetAccountNumber).orElse(null);
            BigDecimal sourceAccountBalance = sourceAccount.getBalance();
            if (hasSufficientBalance(sourceAccountBalance, amount)) {
                sourceAccount.setBalance(sourceAccountBalance.subtract(amount));
                targetAccount.setBalance(targetAccount.getBalance().add(amount));
                accountRepository.save(sourceAccount);
                accountRepository.save(targetAccount);
            } else {
                throw new InvalidUserRequestException(Notification.INSUFFICIENT_FUNDS.message());
            }
        } catch (NullPointerException e) {
            throw new DataRetrievalFailureException(Notification.ACCOUNT_NOT_FOUND.message());
        }
    }

    public boolean hasValidTransferDetails(FundTransferRequest fundTransferRequest) {//TODO refactor targetAcc variable
        Account targetAccount = accountRepository.findAccountByAccountNumber(fundTransferRequest.getTargetAccountNumber())
            .orElseThrow(() -> new DataRetrievalFailureException(Notification.ACCOUNT_NOT_FOUND.message()));
        String targetAccountHolderName =
            targetAccount.getCustomer().getFirstName() + " " + targetAccount.getCustomer().getLastName();

        return isNameMatchingTargetAccountHolderName(fundTransferRequest.getTargetAccountHolderName(), targetAccountHolderName) &&
            isTargetAccountDifferentThanSourceAccount(fundTransferRequest.getTargetAccountNumber(), fundTransferRequest.getAccountNumber())
            && isAmountGreaterThanZero(fundTransferRequest.getAmount());
    }

    private boolean isAmountGreaterThanZero(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean isNameMatchingTargetAccountHolderName(String providedTargetAccountName, String targetAccountHolderName) {
        return providedTargetAccountName.equals(targetAccountHolderName);
    }

    private boolean isTargetAccountDifferentThanSourceAccount(String targetAccountNumber, String sourceAccountNumber) {
        return !targetAccountNumber.equals(sourceAccountNumber);
    }

    @Override
    public void freezeAccount(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() ->
            new DataRetrievalFailureException(Notification.ACCOUNT_NOT_FOUND.message()));
        account.setFrozen(true);
    }

    @Override
    public void reactivateAccount(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() ->
            new DataRetrievalFailureException(Notification.ACCOUNT_NOT_FOUND.message()));
        account.setFrozen(false);
    }

    @Transactional
    @Override
    public void withdraw(String accountNumber, BigDecimal withdrawAmount) {
        if (!isAmountGreaterThanZero(withdrawAmount)) {
            throw new InvalidUserRequestException(Notification.WITHDRAW_INVALID_AMOUNT.message());
        }
        Account account = accountRepository.findAccountByAccountNumber(accountNumber).orElseThrow(() ->
            new DataRetrievalFailureException(Notification.ACCOUNT_NOT_FOUND.message()));
        BigDecimal balance = account.getBalance();
        if (hasSufficientBalance(balance, withdrawAmount)) {
            account.setBalance(balance.subtract(withdrawAmount));
            accountRepository.save(account);
        } else {
            throw new InvalidUserRequestException(Notification.INSUFFICIENT_FUNDS.message());
        }
    }

    private boolean hasSufficientBalance(BigDecimal balance, BigDecimal requestedAmount) {
        return balance.compareTo(requestedAmount) >= 0;
    }

    @Transactional
    @Override
    public void deposit(String accountNumber, BigDecimal depositAmount) {
        if (!isAmountGreaterThanZero(depositAmount)) {
            throw new InvalidUserRequestException(Notification.DEPOSIT_INVALID_AMOUNT.message());
        }
        Account account = accountRepository.findAccountByAccountNumber(accountNumber).orElseThrow(() ->
            new DataRetrievalFailureException(Notification.ACCOUNT_NOT_FOUND.message()));
        account.setBalance(account.getBalance().add(depositAmount));
        accountRepository.save(account);
    }

    @Override
    public Account findAccountAccountNumber(String accountNumber) {
        return accountRepository.findAccountByAccountNumber(accountNumber).orElseThrow(() ->
            new DataRetrievalFailureException(Notification.ACCOUNT_NOT_FOUND.message()));
    }

    @Override
    public List<Account> findAccountsByCustomerId(Long id) {
        return accountRepository.findAccountsByCustomerId(id);
    }
}
