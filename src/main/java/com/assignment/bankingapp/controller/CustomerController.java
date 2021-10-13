package com.assignment.bankingapp.controller;


import com.assignment.bankingapp.entity.Customer;
import com.assignment.bankingapp.entity.CustomerCreationRequest;
import com.assignment.bankingapp.entity.ResponseObject;
import com.assignment.bankingapp.notification.Notification;
import com.assignment.bankingapp.service.AccountService;
import com.assignment.bankingapp.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ResponseObject> newCustomer(@RequestBody CustomerCreationRequest customerCreationRequest){
        Customer newCustomer = customerService.newCustomer(customerCreationRequest);
        return ResponseEntity.ok(
            new ResponseObject(Notification.CUSTOMER_CREATION_SUCCESS.message(), HttpStatus.OK.value(), newCustomer));
    }
}
