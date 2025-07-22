package com.diaconn_mall.website.controller;

import com.diaconn_mall.website.dto.ProductDto;
import com.diaconn_mall.website.entity.Product;
import com.diaconn_mall.website.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    @GetMapping("/banners")
    public ResponseEntity<List<ProductDto>> getBanners() {
        List<ProductDto> banners = productService.getBanners().stream()
                .map(p -> new ProductDto(
                        p.getId(),
                        p.isBanner(),
                        p.getNm(),
                        p.getContentDesc(),
                        p.getCount(),
                        p.getPrice(),
                        resolveImageUrl(p.getImgUrl()),
                        p.getAltText(),
                        p.getState(),
                        p.getCategory()
                ))
                .toList();
        return ResponseEntity.ok(banners);
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductDto>> getProducts() {
        List<ProductDto> products = productService.getProducts().stream()
                .map(p -> new ProductDto(
                        p.getId(),
                        p.isBanner(),
                        p.getNm(),
                        p.getContentDesc(),
                        p.getCount(),
                        p.getPrice(),
                        resolveImageUrl(p.getImgUrl()),
                        p.getAltText(),
                        p.getState(),
                        p.getCategory()
                ))
                .toList();
        return ResponseEntity.ok(products);
    }

    // 상품 카테고리별 조회
    @GetMapping("/products/{category}")
    public ResponseEntity<List<ProductDto>> getCategoryProducts(@PathVariable String category) {
        List<ProductDto> products = productService.getCategoryProducts(category).stream()
                .map(p -> new ProductDto(
                        p.getId(),
                        p.isBanner(),
                        p.getNm(),
                        p.getContentDesc(),
                        p.getCount(),
                        p.getPrice(),
                        resolveImageUrl(p.getImgUrl()),
                        p.getAltText(),
                        p.getState(),
                        p.getCategory()
                ))
                .toList();
        return ResponseEntity.ok(products);
    }

    // 상품 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable int id) {
        Optional<Product> product = productService.findById(id);

        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "상품을 찾을 수 없습니다."));
        }
    }

    // 상품 검색
    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam("q") String keyword) {
        List<Product> products = productService.searchByName(keyword);
        List<ProductDto> responseList = products.stream()
                .map(p -> new ProductDto(
                        p.getId(),
                        p.isBanner(),
                        p.getNm(),
                        p.getContentDesc(),
                        p.getCount(),
                        p.getPrice(),
                        resolveImageUrl(p.getImgUrl()),
                        p.getAltText(),
                        p.getState(),
                        p.getCategory()
                ))
                .toList();

        return ResponseEntity.ok(responseList);
    }

    private String resolveImageUrl(String path) {
        if (path == null || path.isBlank()) return null;
        if (path.startsWith("http")) return path;
        String fullPath = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, path);
        System.out.println("조립된 이미지 URL: " + fullPath);
        return fullPath;
    }
}




