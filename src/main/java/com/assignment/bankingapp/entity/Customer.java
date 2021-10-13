package com.assignment.bankingapp.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDate;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="auth_code")
    private String assignedCustomerCode;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    @Email
    private String email;
    private boolean isAccountActive = true;
    private LocalDate createdAt;

    @OneToMany(mappedBy = "customer")
    @Singular
    private Set<Account> accounts;

    @OneToOne(targetEntity = Address.class, cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address;
}
