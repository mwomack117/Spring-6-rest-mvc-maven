package com.womack.spring6restmvcmaven.repositories;

import com.womack.spring6restmvcmaven.entities.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerRespositoryTest {

    @Autowired
    CustomerRepository customerRespository;

    @Test
    void testSaveCustomer() {
        Customer savedCustomer = customerRespository.save(Customer.builder()
                        .firstName("Mikey")
                        .lastName("Womack")
                .build());

        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getId()).isNotNull();
        assertThat(savedCustomer.getFirstName()).isEqualTo("Mikey");
    }

}