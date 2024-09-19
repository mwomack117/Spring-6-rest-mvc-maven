package com.womack.spring6restmvcmaven.repositories;

import com.womack.spring6restmvcmaven.entities.Beer;
import com.womack.spring6restmvcmaven.model.BeerStyle;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class BeerRepositoryTest {

    @Autowired
    private BeerRepository beerRepository;

    @Test
    void testSavedBeer() {

        Beer savedBeer = beerRepository.save(Beer.builder()
                .beerName("Bells Two Hearted")
                .beerStyle(BeerStyle.IPA)
                .upc("456123987")
                .price(new BigDecimal("12.99"))
                .build());

        beerRepository.flush();

        System.out.println(savedBeer.getId());
        assertThat(savedBeer).isNotNull();
        assertThat(savedBeer.getId()).isNotNull();
        assertThat(savedBeer.getBeerName()).isEqualTo("Bells Two Hearted");
    }

    @Test
    void testSavedBeerNameTooLong() {

        assertThrows(ConstraintViolationException.class, () -> {
            Beer savedBeer = beerRepository.save(Beer.builder()
                    .beerName("Bells Two Hearted asdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdf")
                    .beerStyle(BeerStyle.IPA)
                    .upc("456123987")
                    .price(new BigDecimal("12.99"))
                    .build());

            beerRepository.flush();
        });
    }

}