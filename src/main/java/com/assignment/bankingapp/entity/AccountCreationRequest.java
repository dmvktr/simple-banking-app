package com.assignment.bankingapp.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Currency;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreationRequest {
    @NotNull
    private Currency currency;
    @NotNull
    private AccountType accountType;
}
