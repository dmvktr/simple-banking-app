package com.assignment.bankingapp.entity;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FundTransferRequest extends TransactionRequest{
    @NotNull
    private String targetAccountNumber;
    @NotNull
    private String targetAccountHolderName;
    private String transactionNote;
    
    public FundTransferRequest(@NotNull String accountNumber, @NotNull BigDecimal amount, String targetAccountNumber,
                               String targetAccountHolderName, String transactionNote) {
        super(accountNumber, amount);
        this.targetAccountNumber = targetAccountNumber;
        this.targetAccountHolderName = targetAccountHolderName;
        this.transactionNote = transactionNote;
    }
}
