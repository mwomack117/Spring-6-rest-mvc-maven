package com.womack.spring6restmvcmaven.repositories;

import com.womack.spring6restmvcmaven.entities.BeerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BeerOrderRepository extends JpaRepository<BeerOrder, UUID> {

}
