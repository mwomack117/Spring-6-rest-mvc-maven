package com.womack.spring6restmvcmaven.repositories;

import com.womack.spring6restmvcmaven.entities.Beer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BeerRepository extends JpaRepository<Beer, UUID> {
}
