package com.womack.spring6restmvcmaven.repositories;

import com.womack.spring6restmvcmaven.entities.Beer;
import com.womack.spring6restmvcmaven.entities.BeerOrder;
import com.womack.spring6restmvcmaven.entities.BeerOrderShipment;
import com.womack.spring6restmvcmaven.entities.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

//@DataJpaTest
@SpringBootTest
class BeerOrderRepositoryTest {

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerRepository beerRepository;

    Customer testCustomer;
    Beer testBeer;

    @BeforeEach
    void setup() {
        testCustomer = customerRepository.findAll().getFirst();
        testBeer = beerRepository.findAll().get(2);
    }

    @Transactional
    @Test
    void testBeerOrders() {
        BeerOrder beerOrder = BeerOrder.builder()
                .customerRef("test order")
                .customer(testCustomer)
                .beerOrderShipment(BeerOrderShipment.builder()
                        .trackingNumber("abc123")
                        .build())
                .build();

        BeerOrder savedBeerOrder = beerOrderRepository.save(beerOrder);


        System.out.println(savedBeerOrder);
//        System.out.println(savedBeerOrder.getCustomerRef());
    }
}