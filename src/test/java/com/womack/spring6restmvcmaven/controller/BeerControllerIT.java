package com.womack.spring6restmvcmaven.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.womack.spring6restmvcmaven.entities.Beer;
import com.womack.spring6restmvcmaven.exception.NotFoundException;
import com.womack.spring6restmvcmaven.mappers.BeerMapper;
import com.womack.spring6restmvcmaven.model.BeerDTO;
import com.womack.spring6restmvcmaven.model.BeerStyle;
import com.womack.spring6restmvcmaven.repositories.BeerRepository;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BeerControllerIT {
    @Autowired
    BeerController beerController;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    BeerMapper beerMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    @BeforeEach
    void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Transactional
    @Rollback
    @Test
    void testCreateNewBeer() {
        BeerDTO beerDTO = BeerDTO.builder()
                .beerName("<new beer> Bell's Two Hearted")
                .beerStyle(BeerStyle.IPA)
                .price(new BigDecimal("12.99"))
                .build();

        ResponseEntity responseEntity = beerController.createNewBeer(beerDTO);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();
        String[] locationUUID = responseEntity.getHeaders().getLocation().getPath().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[locationUUID.length - 1]);

        Beer beer = beerRepository.findById(savedUUID).orElseThrow();
        assertThat(beer).isNotNull();
        assertThat(beer.getBeerName()).isEqualTo(beerDTO.getBeerName());
    }

    @Test
    void testListBeers() {
        Page<BeerDTO> dtos = beerController.getAllBeers(null, null, false, 1, 2413);
        assertThat(dtos.getSize()).isEqualTo(1000);
    }

    @Rollback
    @Transactional
    @Test
    void testEmptyList() {
        beerRepository.deleteAll();

        Page<BeerDTO> dtos = beerController.getAllBeers(null, null, false, 1, 25);
        assertThat(dtos.getContent().size()).isEqualTo(0);
    }

    @Test
    void testGetBeerById() {
        Beer beer = beerRepository.findAll().getFirst();
        BeerDTO dto = beerController.getBeerById(beer.getId());
        assertThat(dto.getId()).isEqualTo(beer.getId());
    }

    @Test
    void testBeerIdNotFound() {
        assertThrows(NotFoundException.class, () -> beerController.getBeerById(UUID.randomUUID()));
    }

    @Transactional
    @Rollback
    @Test
    void testUpdateBeerById() {
        Beer beer = beerRepository.findAll().getFirst();
        BeerDTO dto = beerMapper.beerToBeerDTO(beer);

        dto.setId(null);
        dto.setVersion(null);
        final String newBeerName = "[Updated] Hazy Jai Alai";
        dto.setBeerName(newBeerName);

        ResponseEntity responseEntity = beerController.updateById(beer.getId(), dto);
        String code = responseEntity.getStatusCode().toString();
        System.out.println(code);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        Beer updatedBeer = beerRepository.findById(beer.getId()).orElseThrow();
        assertThat(updatedBeer.getBeerName()).isEqualTo(newBeerName);
    }

    @Transactional
    @Rollback
    @Test
    void testPatchBeerById() {
        Beer beer = beerRepository.findAll().getFirst();
        BeerDTO dto = beerMapper.beerToBeerDTO(beer);

        dto.setId(null);
        dto.setVersion(null);
        final String newBeerName = "[Updated] Heady Topper";
        dto.setBeerName(newBeerName);

        ResponseEntity responseEntity = beerController.updateBeerPatchById(beer.getId(), dto);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        Beer updatedBeer = beerRepository.findById(beer.getId()).orElseThrow();
        assertThat(updatedBeer.getBeerName()).isEqualTo(newBeerName);

    }

    @Test
    void testUpdateBeerByIdNotFound() {
        assertThrows(NotFoundException.class, () ->
                beerController.updateById(UUID.randomUUID(), BeerDTO.builder().build()));
    }

    @Test
    void testDeleteBeerByIdNotFound() {
        assertThrows(NotFoundException.class, () ->
                beerController.deleteBeer(UUID.randomUUID()));
    }

    @Transactional
    @Rollback
    @Test
    void testDeleteByIdFound() {
        Beer beer = beerRepository.findAll().getFirst();
        ResponseEntity responseEntity = beerController.deleteBeer(beer.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        assertThat(beerRepository.findById(beer.getId())).isEmpty();
    }

    @Test
    void testPatchBeerBadName() throws Exception {
        Beer testBeer = beerRepository.findAll().getFirst();

        Map<String, Object> beerMap = new HashMap<>();
        beerMap.put("beerName", "new Beer name asdfasdfasdfdafasdfasdfasdfasdfasdfasdfasdf");

        mockMvc.perform(patch(BeerController.BEER_PATH_ID, testBeer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerMap)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testListBeersByName() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                        .queryParam("beerName", "IPA")
                        .queryParam("pageSize", "800"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(329)));
    }

    @Test
    void testListBeersByBeerStyle() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                        .queryParam("beerStyle", BeerStyle.IPA.name())
                        .queryParam("pageSize", "800"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(699)));
    }

    @Test
    void testListBeersByStyleAndName() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                        .queryParam("beerName", "IPA")
                        .queryParam("beerStyle", BeerStyle.IPA.name())
                        .queryParam("pageSize", "800"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(304)));
    }

    @Test
    void tesListBeersByStyleAndNameShowInventoryTrue() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                        .queryParam("beerName", "IPA")
                        .queryParam("beerStyle", BeerStyle.IPA.name())
                        .queryParam("showInventory", "true")
                        .queryParam("pageSize", "800"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(304)))
                .andExpect(jsonPath("$.content[0].quantityOnHand").value(IsNull.notNullValue()));
    }

    @Test
    void tesListBeersByStyleAndNameShowInventoryFalse() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                        .queryParam("beerName", "IPA")
                        .queryParam("beerStyle", BeerStyle.IPA.name())
                        .queryParam("showInventory", "false")
                        .queryParam("pageSize", "800"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(304)))
                .andExpect(jsonPath("$.content[0].quantityOnHand").value(IsNull.nullValue()));
    }

    @Test
    void tesListBeersByStyleAndNameShowInventoryTruePage2() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                        .queryParam("beerName", "IPA")
                        .queryParam("beerStyle", BeerStyle.IPA.name())
                        .queryParam("showInventory", "true")
                        .queryParam("pageNumber", "2")
                        .queryParam("pageSize", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(50)))
                .andExpect(jsonPath("$.content[0].quantityOnHand").value(IsNull.notNullValue()));
    }


}