package com.womack.spring6restmvcmaven.repositories;

import com.womack.spring6restmvcmaven.entities.Beer;
import com.womack.spring6restmvcmaven.entities.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategoryRepositoryTest {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BeerRepository beerRepository;

    Beer testBeer;

    @BeforeEach
    void setup() {
        testBeer = beerRepository.findAll().getFirst();
    }
    @Transactional
    @Test
    void testAddCategory() {
        Category saveCat = categoryRepository.save(Category.builder()
                .description("Ales")
                .build());

        testBeer.addCategory(saveCat);
        Beer saveBeer = beerRepository.save(testBeer);

        System.out.println(saveBeer.getBeerName());
    }

}