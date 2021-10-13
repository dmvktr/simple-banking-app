package com.assignment.bankingapp.controller;

import com.assignment.bankingapp.entity.*;
import com.assignment.bankingapp.notification.Notification;
import com.assignment.bankingapp.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;


@RestController
@RequestMapping("/account")
public class AccountController {

    AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/{userId}/newAccount")
    public ResponseEntity<ResponseObject> newAccount(@PathVariable Long userId,
                                                     @RequestBody AccountCreationRequest accountCreationRequest) {
        Account newAccount = accountService.createNewAccount(userId, accountCreationRequest.getAccountType(),
            accountCreationRequest.getCurrency());
        return ResponseEntity.ok(
            new ResponseObject(Notification.ACCOUNT_CREATION_SUCCESS.message(), HttpStatus.OK.value(), newAccount));
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<ResponseObject> getBalance(@PathVariable Long accountId){
        BigDecimal balance = accountService.getAccountBalance(accountId);
        return ResponseEntity.ok(
            new ResponseObject(Notification.BALANCE_CHECK_SUCCESS.message(), HttpStatus.OK.value(), balance));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Object> withdraw(@Valid @RequestBody FundRequest fundRequest){
        accountService.withdraw(fundRequest.getAccountNumber(), fundRequest.getAmount());
        return ResponseEntity.ok(
            new ResponseObject(Notification.WITHDRAW_SUCCESS.message(), HttpStatus.OK.value(), null));
    }

    @PostMapping("/deposit")
    public ResponseEntity<Object> deposit(@Valid @RequestBody FundRequest fundRequest){
        accountService.deposit(fundRequest.getAccountNumber(), fundRequest.getAmount());
        return new ResponseEntity<>(new ResponseObject(Notification.DEPOSIT_SUCCESS.message(), HttpStatus.OK.value(),
            null), HttpStatus.OK);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Object> transferFunds(@Valid @RequestBody FundTransferRequest fundTransferRequest){
        if(!accountService.hasValidTransferDetails(fundTransferRequest)){
            return ResponseEntity.badRequest().body(
                new ResponseObject(Notification.INVALID_TRANSFER_DETAILS.message(), HttpStatus.BAD_REQUEST.value(),
                    null));
        }
        accountService.transferFunds(fundTransferRequest.getAmount(), fundTransferRequest.getAccountNumber(),
            fundTransferRequest.getTargetAccountNumber());

        return ResponseEntity.ok(new ResponseObject(Notification.TRANSFER_SUCCESS.message(), HttpStatus.OK.value(),
            null));
    }
}
