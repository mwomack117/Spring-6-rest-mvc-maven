package com.womack.spring6restmvcmaven.controller;

import com.womack.spring6restmvcmaven.exception.NotFoundException;
import com.womack.spring6restmvcmaven.model.BeerDTO;
import com.womack.spring6restmvcmaven.model.BeerStyle;
import com.womack.spring6restmvcmaven.services.BeerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BeerController {

    public static final String BEER_PATH = "/api/v1/beer";
    public static final String BEER_PATH_ID = BEER_PATH + "/{beerId}";

    private final BeerService beerService;

    @PostMapping(value = BEER_PATH) //can use either annotations
    //@RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createNewBeer(@Validated @RequestBody BeerDTO beer) {
        BeerDTO savedBeer = beerService.saveNewBeer(beer);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BEER_PATH + "/" + savedBeer.getId().toString());
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @GetMapping(value = BEER_PATH)
    //if not specify @RequestParam("name"), it will default use param name "beerName"
    public Page<BeerDTO> getAllBeers(@RequestParam(required = false) String beerName,
                                     @RequestParam(required = false) BeerStyle beerStyle,
                                     @RequestParam(required = false) Boolean showInventory,
                                     @RequestParam(required = false) Integer pageNumber,
                                     @RequestParam(required = false) Integer pageSize) {
        return beerService.getAllBeers(beerName, beerStyle, showInventory, pageNumber, pageSize);
    }

    @GetMapping(value = BEER_PATH_ID) // 'path' or 'value' annotation not necessary?
    //@RequestMapping(value = "/{beerId}", method = RequestMethod.GET)
    public BeerDTO getBeerById(@PathVariable("beerId") UUID beerId) {
        log.debug("getBeerById 123456- in controller. Id: {}", beerId);
        return beerService.getBeerById(beerId).orElseThrow(NotFoundException::new);
    }

    @PutMapping(value = BEER_PATH_ID)
    public ResponseEntity updateById(@PathVariable("beerId") UUID beerId, @Validated @RequestBody BeerDTO beer) {
        if (beerService.updateBeerById(beerId, beer).isEmpty()) {
            throw new NotFoundException("Beer with id " + beerId + " not found");
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(value = BEER_PATH_ID)
    public ResponseEntity updateBeerPatchById(@PathVariable("beerId") UUID beerId, @RequestBody BeerDTO beer) {
        if (beerService.patchBeerById(beerId, beer).isEmpty()) {
            throw new NotFoundException("Beer with id " + beerId + " not found");
        }

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = BEER_PATH_ID)
    public ResponseEntity deleteBeer(@PathVariable("beerId") UUID beerId) {
        if (!beerService.deleteBeerById(beerId)) {
            throw new NotFoundException("Beer with id " + beerId + " not found");
        }

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


}
