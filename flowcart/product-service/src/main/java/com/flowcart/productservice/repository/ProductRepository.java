package com.flowcart.productservice.repository;

import com.flowcart.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Example:
    // List<Product> findByNameContaining(String name);
}