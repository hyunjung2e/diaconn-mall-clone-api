package com.diaconn_mall.website.controller;

import com.diaconn_mall.website.entity.Product;
import com.diaconn_mall.website.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping("/search")
    public List<Product> search(@RequestParam String q) {
        return service.searchByName(q);
    }
}
