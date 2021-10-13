package com.assignment.bankingapp.notification;

public enum Notification {
    ACCOUNT_NOT_FOUND("The account associated with the provided data was not found!"),
    ACCOUNT_CREATION_SUCCESS("Account creation successful!"),
    CUSTOMER_NOT_FOUND("The customer associated with the provided data was not found!"),
    INSUFFICIENT_FUNDS("Insufficient funds! Please check your account balance!"),
    CUSTOMER_LISTING("Customer listing successful!"),
    CUSTOMER_CREATION_SUCCESS("Customer creation successful!"),
    INVALID_CUSTOMER_DETAILS("Invalid customer details provided!"),
    BALANCE_CHECK_SUCCESS("Balance check successful!"),
    WITHDRAW_INVALID_AMOUNT("Amount to withdraw must be greater than zero!"),
    DEPOSIT_INVALID_AMOUNT("Amount to deposit must be greater than zero!"),
    WITHDRAW_SUCCESS ("Funds successfully withdrawn!"),
    DEPOSIT_SUCCESS("Deposit successful!"),
    INVALID_TRANSFER_DETAILS("Invalid target account details provided!"),
    TRANSFER_SUCCESS("Funds successfully transferred!");

    private final String message;

    Notification(String message) {
        this.message = message;
    }

    public String message(){return this.message;}
}