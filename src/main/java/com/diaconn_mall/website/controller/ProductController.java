package com.diaconn_mall.website.controller;

import com.diaconn_mall.website.dto.ProductResponse;
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
    public ResponseEntity<List<ProductResponse>> getBanners() {
        List<ProductResponse> banners = productService.getBanners().stream()
                .map(p -> {
                    return new ProductResponse(p.getId(),
                            p.getNm(),
                            p.getDesc(),
                            p.getPrice(),
                            resolveImageUrl(p.getImgUrl()),
                            p.getAltText());
                })
                .toList();
        return ResponseEntity.ok(banners);
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getProducts() {
        List<ProductResponse> products = productService.getProducts().stream()
                .map(p -> new ProductResponse(
                        p.getId(),
                        p.getNm(),
                        p.getDesc(),
                        p.getPrice(),
                        resolveImageUrl(p.getImgUrl()),
                        p.getAltText()
                ))
        .toList();
        return ResponseEntity.ok(products);
    }

    private String resolveImageUrl(String path) {
        if (path.startsWith("http")) return path;
        String fullPath = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, path);
        System.out.println("조립된 이미지 URL: " + fullPath);
        return fullPath;
    }


    // 상품 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.findById(id);

        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "상품을 찾을 수 없습니다."));
        }
    }
}
