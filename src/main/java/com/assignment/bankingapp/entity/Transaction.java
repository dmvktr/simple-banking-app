package com.assignment.bankingapp.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @GeneratedValue
    @Id
    private Long id;
    private LocalDateTime time;
    private String note;
    private BigDecimal amount;
    private TransactionType type;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}
