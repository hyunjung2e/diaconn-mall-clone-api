package com.diaconn_mall.website.service;

import com.diaconn_mall.website.entity.Product;
import com.diaconn_mall.website.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> searchByName(String keyword) {
        return productRepository.findByNmContaining(keyword);
    }

    public List<Product> getAllBanners() {
        List<Product> banners = productRepository.findAll().stream()
                .filter(Product::isBanner)
                .toList();
        return banners;
    }

    public List<Product> getProductImages() {
        return productRepository.findByIsBannerFalse();
    }
}
