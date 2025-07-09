package com.diaconn_mall.website.service;

import com.diaconn_mall.website.entity.Product;
import com.diaconn_mall.website.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> searchByName(String keyword) {
        return productRepository.findByNmContaining(keyword);
    }

    public List<Product> getBanners() {
        return productRepository.findAll().stream()
                .filter(Product::isBanner)
                .toList();
    }

    public List<Product> getProducts() {
        return productRepository.findAll().stream()
                .filter(p -> !p.isBanner())
                .toList();
    }

    // 상품 ID로 단건조회
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }
}
