package com.womack.spring6restmvcmaven.controller;

import com.womack.spring6restmvcmaven.entities.Customer;
import com.womack.spring6restmvcmaven.exception.NotFoundException;
import com.womack.spring6restmvcmaven.mappers.CustomerMapperImpl;
import com.womack.spring6restmvcmaven.model.CustomerDTO;
import com.womack.spring6restmvcmaven.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerControllerIT {

    @Autowired
    CustomerController customerController;

    @Autowired
    CustomerRepository customerRespository;

    @Autowired
    private CustomerMapperImpl customerMapperImpl;

    @Test
    void testGetAllCustomers() {
        List<CustomerDTO> dtos = customerController.listAllCustomers();

        assertNotNull(dtos);
        assertThat(dtos.size()).isEqualTo(3);
    }

    @Rollback
    @Transactional
    @Test
    void testEmptyList() {
        customerRespository.deleteAll();
        List<CustomerDTO> dtos = customerController.listAllCustomers();

        assertThat(dtos.size()).isEqualTo(0);
    }

    @Test
    void testGetCustomerById() {
        Customer customer = customerRespository.findAll().getFirst();
        CustomerDTO dto = customerController.getCustomerById(customer.getId());
        System.out.println(dto.toString());
        assertNotNull(dto);
        assertThat(dto.getId()).isEqualTo(customer.getId());
    }

    @Test
    void testCustomerIdNotFound() {
        assertThrows(NotFoundException.class, () -> customerController.getCustomerById(UUID.randomUUID()));
    }

    @Transactional
    @Rollback
    @Test
    void testCreateCustomer() {
        CustomerDTO customerDTO = CustomerDTO.builder().
                firstName("John")
                .lastName("Deere")
                .build();

        ResponseEntity responseEntity = customerController.handlePost(customerDTO);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.valueOf(201));
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();
        String[] locationPath = responseEntity.getHeaders().getLocation().toString().split("/");
        UUID savedUUID = UUID.fromString(locationPath[locationPath.length - 1]);

        Customer customer = customerRespository.findById(savedUUID).orElseThrow();
        assertThat(customer).isNotNull();
        assertThat(customer.getFirstName()).isEqualTo(customerDTO.getFirstName());
    }

    @Transactional
    @Rollback
    @Test
    void testUpdateCustomer() {
        Customer customer = customerRespository.findAll().getFirst();
        CustomerDTO customerDTO = customerMapperImpl.customerToCustomerDTO(customer);

        customerDTO.setId(null);
        customerDTO.setVersion(null);
        final String newFirstName = "updated - Johnny";
        customerDTO.setFirstName(newFirstName);

        ResponseEntity responseEntity = customerController.updateCustomer(customer.getId(), customerDTO);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.valueOf(204));
        Customer updatedCustomer = customerRespository.findById(customer.getId()).orElseThrow();
        assertThat(updatedCustomer.getFirstName()).isEqualTo(newFirstName);
    }

    @Test
    void testUpdateCustomerNotFound() {
        assertThrows(NotFoundException.class, () ->
                customerController.updateCustomer(UUID.randomUUID(), null));
    }

    @Transactional
    @Rollback
    @Test
    void testPatchCustomer() {
        Customer customer = customerRespository.findAll().getFirst();
        CustomerDTO customerDTO = customerMapperImpl.customerToCustomerDTO(customer);

        final String newFirstName = "updated - Johnny";
        customerDTO.setFirstName(newFirstName);

        ResponseEntity responseEntity = customerController.updateCustomer(customer.getId(), customerDTO);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.valueOf(204));
        Customer updatedCustomer = customerRespository.findById(customer.getId()).orElseThrow();
        assertThat(updatedCustomer.getFirstName()).isEqualTo(newFirstName);
        assertThat(updatedCustomer.getLastName()).isEqualTo(customer.getLastName());
    }

    @Transactional
    @Rollback
    @Test
    void testDeleteCustomerById() {
        Customer customer = customerRespository.findAll().getFirst();
        ResponseEntity responseEntity = customerController.deleteCustomer(customer.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.valueOf(204));
        assertThat(customerRespository.findById(customer.getId())).isEmpty();
    }

    @Test
    void testDeleteCustomerIdNotFound() {
        assertThrows(NotFoundException.class, () ->
                customerController.deleteCustomer(UUID.randomUUID()));
    }

}