package com.assignment.bankingapp.service.implementation;

import com.assignment.bankingapp.entity.Customer;
import com.assignment.bankingapp.entity.CustomerCreationRequest;
import com.assignment.bankingapp.exception.InvalidUserRequestException;
import com.assignment.bankingapp.notification.Notification;
import com.assignment.bankingapp.repository.CustomerRepository;
import com.assignment.bankingapp.service.CustomerService;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CustomerServiceImplementation implements CustomerService {

    CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;

    public CustomerServiceImplementation(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    public Customer newCustomer(CustomerCreationRequest customerCreationRequest) {
        Customer customer = Customer.builder()
            .email(customerCreationRequest.getEmail())
            .assignedCustomerCode(customerCreationRequest.getAssignedCustomerCode())
            .firstName(customerCreationRequest.getFirstName())
            .lastName(customerCreationRequest.getLastName())
            .phoneNumber(customerCreationRequest.getPhoneNumber())
            .createdAt(LocalDate.now())
            .address(customerCreationRequest.getAddress())
            .password(passwordEncoder.encode(customerCreationRequest.getPassword()))
            .build();
        return customerRepository.save(customer);
    }

    @Override
    public Customer findById(Long customerId) {
        return customerRepository.findById(customerId).orElseThrow(() ->
            new DataRetrievalFailureException(Notification.CUSTOMER_NOT_FOUND.message()));
    }

    @Override
    public Customer findByEmailAddress(String email) {
        if(email == null || email.equals("")){
            throw new InvalidUserRequestException(Notification.INVALID_CUSTOMER_DETAILS.message());
        }
        return customerRepository.findCustomerByEmail(email).orElseThrow(() ->
            new DataRetrievalFailureException(Notification.CUSTOMER_NOT_FOUND.message()));
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public void saveCustomer(Customer customer) {
        if(customer == null){
            throw new InvalidUserRequestException(Notification.INVALID_CUSTOMER_DETAILS.message());
        }
        customerRepository.save(customer);
    }

    @Override
    public void deleteAll() {
        customerRepository.deleteAll();
    }
}
