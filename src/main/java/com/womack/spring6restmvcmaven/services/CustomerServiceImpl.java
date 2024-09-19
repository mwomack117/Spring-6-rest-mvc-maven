package com.womack.spring6restmvcmaven.services;

import com.womack.spring6restmvcmaven.exception.NotFoundException;
import com.womack.spring6restmvcmaven.model.CustomerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    private final Map<UUID, CustomerDTO> customersMap;

    public CustomerServiceImpl() {
        customersMap = new HashMap<>();

        CustomerDTO customer1 = CustomerDTO.builder()
                .firstName("Michael")
                .lastName("Womack")
                .id(UUID.randomUUID())
                .version(1)
                .createDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        CustomerDTO customer2 = CustomerDTO.builder()
                .firstName("Emmie")
                .lastName("Laroosky")
                .id(UUID.randomUUID())
                .version(1)
                .createDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        customersMap.put(customer1.getId(), customer1);
        customersMap.put(customer2.getId(), customer2);
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return new ArrayList<>(customersMap.values());
    }

    @Override
    public Optional<CustomerDTO> getCustomerById(UUID id) {
        log.debug("In Service. Get customer by id: {}", id);

        // I added this for incorrect id passed -> 404 instead of 500. Maybe not right way to handle?
        if (!customersMap.containsValue(id)) throw new NotFoundException();
        return Optional.of(customersMap.get(id));
    }

    @Override
    public CustomerDTO saveNewCustomer(CustomerDTO customer) {

        CustomerDTO savedCustomer = CustomerDTO.builder()
                .id(UUID.randomUUID())
                .version(1)
                .updateDate(LocalDateTime.now())
                .createDate(LocalDateTime.now())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .build();

        customersMap.put(savedCustomer.getId(), savedCustomer);

        return savedCustomer;
    }

    @Override
    public Optional<CustomerDTO> updateCustomerById(UUID customerId, CustomerDTO customer) {
        CustomerDTO existingCustomer = customersMap.get(customerId);
        if (customer.getFirstName() != null) existingCustomer.setFirstName(customer.getFirstName());
        if (customer.getLastName() != null) existingCustomer.setLastName(customer.getLastName());

        existingCustomer.setUpdateDate(LocalDateTime.now());

        return Optional.of(existingCustomer);
    }

    @Override
    public Optional<CustomerDTO> patchCustomerById(UUID customerId, CustomerDTO customer) {
        CustomerDTO existingCustomer = customersMap.get(customerId);
        if (StringUtils.hasText(customer.getFirstName())) existingCustomer.setFirstName(customer.getFirstName());
        if (customer.getLastName() != null) existingCustomer.setLastName(customer.getLastName());

        existingCustomer.setUpdateDate(LocalDateTime.now());

        return Optional.of(existingCustomer);
    }

    @Override
    public Boolean deleteCustomerById(UUID customerId) {
        customersMap.remove(customerId);

        return true;
    }
}
