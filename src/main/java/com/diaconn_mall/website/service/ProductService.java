package com.diaconn_mall.website.service;

import com.diaconn_mall.website.entity.Product;
import com.diaconn_mall.website.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> searchByName(String keyword) {
        return repository.findByNmContaining(keyword);
    }
}
