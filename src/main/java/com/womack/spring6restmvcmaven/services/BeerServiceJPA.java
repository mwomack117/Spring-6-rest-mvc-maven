package com.womack.spring6restmvcmaven.services;

import com.womack.spring6restmvcmaven.entities.Beer;
import com.womack.spring6restmvcmaven.mappers.BeerMapper;
import com.womack.spring6restmvcmaven.model.BeerDTO;
import com.womack.spring6restmvcmaven.model.BeerStyle;
import com.womack.spring6restmvcmaven.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class BeerServiceJPA implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 25;


    @Override
    public List<BeerDTO> getAllBeers(String beerName, BeerStyle beerStyle, Boolean showInventory,
                                     Integer pageNumber, Integer pageSize) {

        PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);

        List<Beer> beerList;

        if (StringUtils.hasText(beerName) && beerStyle == null) {
            beerList = listBeersByName(beerName);
        } else if (!StringUtils.hasText(beerName) && beerStyle != null) {
            beerList = listBeersByStyle(beerStyle);
        } else if (StringUtils.hasText(beerName) && beerStyle != null) {
            beerList = listBeersByNameAndStyle(beerName, beerStyle);
        } else {
            beerList = beerRepository.findAll();
        }

        if (showInventory != null && !showInventory) {
            beerList.forEach(beer -> beer.setQuantityOnHand(null));
        }

        return beerList.stream()
                .map(beerMapper::beerToBeerDTO)
                .collect(Collectors.toList());
//         .map(b -> beerMapper.beerToBeerDTO(b)) - what the method ref does above
    }

    public PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {
        int queryPageNumber;
        int queryPageSize;

        if (pageNumber != null && pageNumber > 0) {
            queryPageNumber = pageNumber - 1;
        } else {
            queryPageNumber = DEFAULT_PAGE;
        }

        if (pageSize == null) {
            queryPageSize = DEFAULT_PAGE_SIZE;
        } else {
            if (pageSize > 1000) {
                queryPageSize = 1000;
            } else {
                queryPageSize = pageSize;
            }
        }

        return PageRequest.of(queryPageNumber, queryPageSize);
    }

    private List<Beer> listBeersByNameAndStyle(String beerName, BeerStyle beerStyle) {
        return beerRepository.findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle("%" + beerName + "%", beerStyle);
    }

    public List<Beer> listBeersByStyle(BeerStyle beerStyle) {
        return beerRepository.findAllByBeerStyle(beerStyle);
    }

    public List<Beer> listBeersByName(String name) {
        return beerRepository.findAllByBeerNameIsLikeIgnoreCase("%" + name + "%");
    }

    @Override
    public Optional<BeerDTO> getBeerById(UUID id) {
        return Optional.ofNullable(beerMapper.beerToBeerDTO(beerRepository.findById(id)
                .orElse(null)));
    }

    @Override
    public BeerDTO saveNewBeer(BeerDTO beer) {
        beer.setCreatedDate(LocalDateTime.now());
        return beerMapper.beerToBeerDTO(beerRepository.save(beerMapper.beerDtoToBeer(beer)));
    }

    @Override
    public Optional<BeerDTO> updateBeerById(UUID beerId, BeerDTO beer) {
        AtomicReference<Optional<BeerDTO>> atomicRefBeerDTO = new AtomicReference<>();

        beerRepository.findById(beerId).ifPresentOrElse(foundBeer -> {
            foundBeer.setBeerName(beer.getBeerName());
            foundBeer.setBeerStyle(beer.getBeerStyle());
            foundBeer.setQuantityOnHand(beer.getQuantityOnHand());
            foundBeer.setPrice(beer.getPrice());
            foundBeer.setUpc(beer.getUpc());
            foundBeer.setUpdateDate(LocalDateTime.now());

            // beerRepository.save(foundBeer);
            atomicRefBeerDTO.set(Optional.of(beerMapper
                    .beerToBeerDTO(beerRepository.save(foundBeer))));
        }, () -> atomicRefBeerDTO.set(Optional.empty()));

        return atomicRefBeerDTO.get();
    }

    @Override
    public Optional<BeerDTO> patchBeerById(UUID beerId, BeerDTO beer) {
        AtomicReference<Optional<BeerDTO>> atomicRefBeerDTO = new AtomicReference<>();

        beerRepository.findById(beerId).ifPresentOrElse(foundBeer -> {
            if (StringUtils.hasText(beer.getBeerName())) foundBeer.setBeerName(beer.getBeerName());
            if (beer.getBeerStyle() != null) foundBeer.setBeerStyle(beer.getBeerStyle());
            if (beer.getQuantityOnHand() != null) foundBeer.setQuantityOnHand(beer.getQuantityOnHand());
            if (beer.getPrice() != null) foundBeer.setPrice(beer.getPrice());
            if (StringUtils.hasText(beer.getUpc())) foundBeer.setUpc(beer.getUpc());
            foundBeer.setUpdateDate(LocalDateTime.now());

            atomicRefBeerDTO.set(Optional.of(beerMapper.
                    beerToBeerDTO(beerRepository.save(foundBeer))));
        }, () -> atomicRefBeerDTO.set(Optional.empty()));

        return atomicRefBeerDTO.get();
    }

    @Override
    public Boolean deleteBeerById(UUID beerId) {
        if (beerRepository.existsById(beerId)) {
            beerRepository.deleteById(beerId);
            return true;
        }
        return false;
    }
}
