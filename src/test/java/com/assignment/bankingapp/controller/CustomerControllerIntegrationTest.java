package com.assignment.bankingapp.controller;

import com.assignment.bankingapp.entity.*;
import com.assignment.bankingapp.notification.Notification;
import com.assignment.bankingapp.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(value = "classpath:application.properties")
@AutoConfigureMockMvc
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        Address london = Address.builder().
            city("London")
            .country("England")
            .zipcode("SW1P 2JJ")
            .street("Abbey Orchard Street")
            .houseNumber("2")
            .build();

        Customer matthew = Customer.builder()
            .email("matthew@example.com")
            .assignedCustomerCode("774464489")
            .password(passwordEncoder.encode("hello world!"))
            .phoneNumber("+462077477777")
            .createdAt(LocalDate.now())
            .firstName("Matthew")
            .lastName("Baker")
            .address(london)
            .build();

        customerService.saveCustomer(matthew);
    }

    @AfterEach
    void tearDown() {
        customerService.deleteAll();
    }

    @Test
    void newCustomer_returnsBadRequest_whenRequestBodyIsMissing() throws Exception {
        mockMvc.perform(post("/customer/new")
                .contentType("application/json"))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    void newCustomer_returnsBadRequest_whenRequestBodyObjectFieldIsNull() throws Exception {
        CustomerCreationRequest customerCreationRequest = CustomerCreationRequest.builder()
            .email(null)
            .assignedCustomerCode(null)
            .password("testpassword134f")
            .phoneNumber("+443357477777")
            .firstName("Matthew")
            .lastName("Baker")
            .address(new Address())
            .build();

        mockMvc.perform(post("/customer/new")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(customerCreationRequest)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    void newCustomer_persisted_whenRequestSuccessful() throws Exception {
        CustomerCreationRequest customerCreationRequest = CustomerCreationRequest.builder()
            .email("david@example.com")
            .assignedCustomerCode("774464489")
            .password("testpassword134f")
            .phoneNumber("+443357477777")
            .firstName("Matthew")
            .lastName("Baker")
            .address(new Address())
            .build();

        mockMvc.perform(post("/customer/new")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(customerCreationRequest)))
            .andDo(print())
            .andExpect(status().isOk());
        Customer newCustomer = customerService.findByEmailAddress("david@example.com");
        assertAll("fields are persisted", () -> {
            assertEquals("+443357477777", newCustomer.getPhoneNumber());
            assertEquals("Matthew", newCustomer.getFirstName());
            assertEquals("Baker", newCustomer.getLastName());
        });
    }
    @Test
    void newCustomer_returnsReturnsJsonResponseBodyWithStatusOk_whenSuccessful() throws Exception {
        CustomerCreationRequest customerCreationRequest = CustomerCreationRequest.builder()
            .email("david@example.com")
            .assignedCustomerCode("774464489")
            .password("testpassword134f")
            .phoneNumber("+443357477777")
            .firstName("Matthew")
            .lastName("Baker")
            .address(new Address())
            .build();
        MvcResult result = mockMvc.perform(post("/customer/new")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(customerCreationRequest)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        Customer newCustomer = customerService.findByEmailAddress("david@example.com");
        ResponseObject expectedResponseBody =
            new ResponseObject(Notification.CUSTOMER_CREATION_SUCCESS.message(), 200, newCustomer);
        String actualResponseBody = result.getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedResponseBody), actualResponseBody);
    }

    @Test
    void getAllCustomers_returnsReturnsJsonResponseBodyWithStatusOk_whenSuccessful() throws Exception {
        List<Customer> customers = customerService.getAllCustomers();
        MvcResult result = mockMvc.perform(get("/customer/all")
                .contentType("application/json"))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        ResponseObject expectedResponseBody =
            new ResponseObject(Notification.CUSTOMER_LISTING_SUCCESS.message(), 200, customers);
        String actualResponseBody = result.getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedResponseBody), actualResponseBody);
    }
}