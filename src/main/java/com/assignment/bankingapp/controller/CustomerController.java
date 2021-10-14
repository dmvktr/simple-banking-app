package com.assignment.bankingapp.controller;


import com.assignment.bankingapp.entity.Customer;
import com.assignment.bankingapp.entity.CustomerCreationRequest;
import com.assignment.bankingapp.entity.ResponseObject;
import com.assignment.bankingapp.notification.Notification;
import com.assignment.bankingapp.service.AccountService;
import com.assignment.bankingapp.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    AccountService accountService;
    CustomerService customerService;

    public CustomerController(AccountService accountService, CustomerService customerService) {
        this.accountService = accountService;
        this.customerService = customerService;
    }

    @PostMapping("/new")
    public ResponseEntity<ResponseObject> newCustomer(@Valid @RequestBody CustomerCreationRequest customerCreationRequest){
        Customer newCustomer = customerService.newCustomer(customerCreationRequest);
        return ResponseEntity.ok(
            new ResponseObject(Notification.CUSTOMER_CREATION_SUCCESS.message(), HttpStatus.OK.value(), newCustomer));
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseObject> getAllCustomers(){
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(
            new ResponseObject(Notification.CUSTOMER_LISTING_SUCCESS.message(), HttpStatus.OK.value(), customers));
    }
}
