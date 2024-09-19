package com.womack.spring6restmvcmaven.mappers;

import com.womack.spring6restmvcmaven.entities.Beer;
import com.womack.spring6restmvcmaven.model.BeerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface BeerMapper {

    Beer beerDtoToBeer(BeerDTO dto);

    BeerDTO beerToBeerDTO(Beer beer);
}
