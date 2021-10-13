package com.assignment.bankingapp.entity;

import java.math.BigDecimal;

public class FundRequest extends TransactionRequest{
    public FundRequest(String accountNumber, BigDecimal amount) {
        super(accountNumber, amount);
    }
}
