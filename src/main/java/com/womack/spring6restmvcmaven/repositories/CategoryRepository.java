package com.womack.spring6restmvcmaven.repositories;

import com.womack.spring6restmvcmaven.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
