package com.assignment.bankingapp.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class TransactionRequest {
    @NotNull
    private String accountNumber;
    @NotNull
    private BigDecimal amount;
}
