package com.diaconn_mall.website.controller;

import com.diaconn_mall.website.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<ImageResponse>> getBanners() {
        List<ImageResponse> banners = productService.getAllBanners().stream()
                .map(p -> {
                    String imageUrl = resolveImageUrl(p.getImgUrl());
                    return new ImageResponse(p.getId(), imageUrl, p.getAltText());
                })
                .toList();
        return ResponseEntity.ok(banners);
    }

    @GetMapping("/products")
    public ResponseEntity<List<ImageResponse>> getProducts() {
        List<ImageResponse> products = productService.getProductImages().stream()
                .map(p -> {
                    String imageUrl = resolveImageUrl("banner/" + p.getImgUrl());
                    return new ImageResponse(p.getId(), imageUrl, p.getAltText());
                })
                .toList();
        return ResponseEntity.ok(products);
    }

    // 절대경로인지 검사
    private String resolveImageUrl(String path) {
        if (path.startsWith("http")) return path;
        String fullPath = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, path);
        System.out.println("조립된 이미지 URL: " + fullPath);
        return fullPath;
    }


    public record ImageResponse(Integer id, String imageUrl, String altText) {}
}
