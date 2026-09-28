package com.example.wastewater.repository;

import com.example.wastewater.entity.Product;
import com.example.wastewater.enums.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductCode(String productCode);
    List<Product> findByType(ProductType type);
    List<Product> findByActiveTrue();
}
