package com.womack.spring6restmvcmaven.services;

import com.womack.spring6restmvcmaven.mappers.BeerMapper;
import com.womack.spring6restmvcmaven.model.BeerDTO;
import com.womack.spring6restmvcmaven.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
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

    @Override
    public List<BeerDTO> getAllBeers() {
        return beerRepository.findAll()
                .stream().map(beerMapper::beerToBeerDTO)
                .collect(Collectors.toList());
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
        if(beerRepository.existsById(beerId)) {
            beerRepository.deleteById(beerId);
            return true;
        }
        return false;
    }
}
