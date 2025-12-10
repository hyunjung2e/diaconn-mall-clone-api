package com.diaconn_mall.website.controller;

import com.diaconn_mall.website.dto.BannerDto;
import com.diaconn_mall.website.dto.ProductDto;
import com.diaconn_mall.website.entity.Product;
import com.diaconn_mall.website.service.BannerService;
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
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;
    private final BannerService bannerService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    @GetMapping("/banners")
    public ResponseEntity<List<BannerDto>> getBanners() {
        List<BannerDto> banners = bannerService.getBanners().stream()
                .map(b -> new BannerDto(
                        b.getId(),
                        b.getNm(),
                        b.getImageUrl(),
                        b.getLinkUrl(),
                        b.getIsActive(),
                        b.getStartDate(),
                        b.getEndDate(),
                        b.getRegPgm(),
                        b.getRegUsrNo(),
                        b.getRegDttm(),
                        b.getUpdPgm(),
                        b.getUpdUsrNo(),
                        b.getUpdDttm()
                ))
                .toList();
        return ResponseEntity.ok(banners);
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductDto>> getProducts() {
        List<ProductDto> products = productService.getProducts().stream()
                .map(p -> new ProductDto(
                        p.getId(),
                        p.getNm(),
                        p.getCount(),
                        p.getPrice(),
                        p.getImgUrl(),
                        p.getState(),
                        p.getAltText(),
                        p.getCategory(),
                        p.getContentDesc(),
                        p.getRegPgm(),
                        p.getRegUsrNo(),
                        p.getRegDttm(),
                        p.getUpdPgm(),
                        p.getUpdUsrNo(),
                        p.getUpdDttm()
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
                        p.getNm(),
                        p.getCount(),
                        p.getPrice(),
                        p.getImgUrl(),
                        p.getState(),
                        p.getAltText(),
                        p.getCategory(),
                        p.getContentDesc(),
                        p.getRegPgm(),
                        p.getRegUsrNo(),
                        p.getRegDttm(),
                        p.getUpdPgm(),
                        p.getUpdUsrNo(),
                        p.getUpdDttm()
                ))
                .toList();
        return ResponseEntity.ok(products);
    }

    // 상품 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.findById(id);

        if (product.isPresent()) {
            Product p = product.get();
            return ResponseEntity.ok(new ProductDto(
                    p.getId(),
                    p.getNm(),
                    p.getCount(),
                    p.getPrice(),
                    p.getImgUrl(),
                    p.getState(),
                    p.getAltText(),
                    p.getCategory(),
                    p.getContentDesc(),
                    p.getRegPgm(),
                    p.getRegUsrNo(),
                    p.getRegDttm(),
                    p.getUpdPgm(),
                    p.getUpdUsrNo(),
                    p.getUpdDttm()
            ));
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
                        p.getNm(),
                        p.getCount(),
                        p.getPrice(),
                        p.getImgUrl(),
                        p.getState(),
                        p.getAltText(),
                        p.getCategory(),
                        p.getContentDesc(),
                        p.getRegPgm(),
                        p.getRegUsrNo(),
                        p.getRegDttm(),
                        p.getUpdPgm(),
                        p.getUpdUsrNo(),
                        p.getUpdDttm()
                ))
                .toList();

        return ResponseEntity.ok(responseList);
    }
}




