package com.womack.spring6restmvcmaven.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.womack.spring6restmvcmaven.model.CustomerDTO;
import com.womack.spring6restmvcmaven.services.CustomerService;
import com.womack.spring6restmvcmaven.services.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CustomerService customerService;

    CustomerServiceImpl customerServiceImpl;

    @Captor
    ArgumentCaptor<UUID> uuidCaptor;

    @Captor
    ArgumentCaptor<CustomerDTO> customerCaptor;

    @BeforeEach
    void setUp() {
        customerServiceImpl = new CustomerServiceImpl();
    }

    @Test
    void reateNewCustomerTest() throws Exception {
        CustomerDTO customer = customerServiceImpl.getAllCustomers().get(0);
        customer.setId(null);

        given(customerService.saveNewCustomer(any(CustomerDTO.class)))
                .willReturn(customerServiceImpl.getAllCustomers().get(1));

        mockMvc.perform(post(CustomerController.CUSTOMER_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

    }

    @Test
    void listAllCustomersTest() throws Exception {
        given(customerService.getAllCustomers()).willReturn(customerServiceImpl.getAllCustomers());

        mockMvc.perform(get(CustomerController.CUSTOMER_PATH)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(equalTo(2))))
                .andExpect(jsonPath("$.[*].id").isNotEmpty());
    }

    @Test
    void getCustomerByIdTest() throws Exception {
        CustomerDTO testCustomer = customerServiceImpl.getAllCustomers().getFirst();

        given(customerService.getCustomerById(testCustomer.getId())).willReturn(Optional.of(testCustomer));

        mockMvc.perform(get(CustomerController.CUSTOMER_PATH_ID, testCustomer.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testCustomer.getId().toString())))
                .andExpect(jsonPath("$.firstName", is(testCustomer.getFirstName())))
                .andExpect(jsonPath("$.lastName", allOf(is(testCustomer.getLastName()), isA(String.class))));
    }

    @Test
    void getCustomerByIdNotFoundTest() throws Exception {
        given(customerService.getCustomerById(any(UUID.class))).willReturn(Optional.empty());

        mockMvc.perform(get(CustomerController.CUSTOMER_PATH_ID, UUID.randomUUID().toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCustomerTest() throws Exception {
        CustomerDTO testCustomer = customerServiceImpl.getAllCustomers().getFirst();

        given(customerService.updateCustomerById(any(), any())).willReturn(Optional.of(testCustomer));

        mockMvc.perform(put(CustomerController.CUSTOMER_PATH_ID, testCustomer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCustomer)))
                .andDo(print())
                .andExpect(status().isNoContent());

        //verify(customerService).updateCustomerById(any(UUID.class), any(Customer.class));
        verify(customerService).updateCustomerById(uuidCaptor.capture(), customerCaptor.capture());

        assertThat(uuidCaptor.getValue()).isEqualTo(testCustomer.getId());
        assertThat(customerCaptor.getValue().getFirstName()).isEqualTo(testCustomer.getFirstName());
    }

    @Test
    void patchCustomerTest() throws Exception {
        CustomerDTO testCustomer = customerServiceImpl.getAllCustomers().getFirst();

        given(customerService.patchCustomerById(any(), any())).willReturn(Optional.of(testCustomer));

        Map<String, Object> customerMap = new HashMap<>();
        customerMap.put("firstName", "Stephen");

        mockMvc.perform(patch(CustomerController.CUSTOMER_PATH_ID, testCustomer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerMap)))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(customerService).patchCustomerById(uuidCaptor.capture(), customerCaptor.capture());

        assertThat(uuidCaptor.getValue()).isEqualTo(testCustomer.getId());
        assertThat(customerCaptor.getValue().getFirstName()).isEqualTo(customerMap.get("firstName"));
    }

    @Test
    void deleteCustomerByIdTest() throws Exception {
        CustomerDTO testCustomer = customerServiceImpl.getAllCustomers().getFirst();

        given(customerService.deleteCustomerById(any(UUID.class))).willReturn(true);

        mockMvc.perform(delete(CustomerController.CUSTOMER_PATH_ID, testCustomer.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());

        //ArgumentCaptor<UUID> uuidCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(customerService).deleteCustomerById(uuidCaptor.capture());

        assertThat(testCustomer.getId()).isEqualTo(uuidCaptor.getValue());
    }
}