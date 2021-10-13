package com.assignment.bankingapp.controller;

import com.assignment.bankingapp.service.AccountService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/account")
public class AccountController {

    AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }
}
