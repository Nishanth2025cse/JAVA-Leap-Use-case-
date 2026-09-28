package com.example.wastewater.service;

import com.example.wastewater.entity.Product;
import com.example.wastewater.enums.ProductType;
import com.example.wastewater.exception.BadRequestException;
import com.example.wastewater.exception.ResourceNotFoundException;
import com.example.wastewater.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public Product createProduct(Product product) {
        if (productRepository.findByProductCode(product.getProductCode()).isPresent()) {
            throw new BadRequestException("Product code already exists: " + product.getProductCode());
        }
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product details) {
        Product existing = getProductById(id);
        existing.setProductName(details.getProductName());
        existing.setType(details.getType());
        existing.setDescription(details.getDescription());
        existing.setUnit(details.getUnit());
        existing.setPrice(details.getPrice());
        existing.setTaxPercentage(details.getTaxPercentage());
        existing.setActive(details.isActive());
        return productRepository.save(existing);
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    public List<Product> getProductsByType(ProductType type) {
        return productRepository.findByType(type);
    }
}
