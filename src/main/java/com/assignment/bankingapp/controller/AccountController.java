package com.assignment.bankingapp.controller;

import com.assignment.bankingapp.entity.Account;
import com.assignment.bankingapp.entity.AccountCreationRequest;
import com.assignment.bankingapp.entity.ResponseObject;
import com.assignment.bankingapp.notification.Notification;
import com.assignment.bankingapp.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
