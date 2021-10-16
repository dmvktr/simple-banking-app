package com.assignment.bankingapp.entity;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FundTransferRequest extends TransactionRequest{
    @NotNull
    private String recipientAccountNumber;
    @NotNull
    private String recipientAccountHolderName;
    private String transactionNote;
    
    public FundTransferRequest(@NotNull String accountNumber, @NotNull BigDecimal amount, String recipientAccountNumber,
                               String recipientAccountHolderName, String transactionNote) {
        super(accountNumber, amount);
        this.recipientAccountNumber = recipientAccountNumber;
        this.recipientAccountHolderName = recipientAccountHolderName;
        this.transactionNote = transactionNote;
    }
}
