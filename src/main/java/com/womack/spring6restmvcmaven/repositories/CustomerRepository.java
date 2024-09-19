package com.womack.spring6restmvcmaven.repositories;

import com.womack.spring6restmvcmaven.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
}
