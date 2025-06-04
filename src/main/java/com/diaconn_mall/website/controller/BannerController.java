package com.diaconn_mall.website.controller;
import com.diaconn_mall.website.service.BannerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/banners")
public class BannerController {

    private final BannerService bannerService;

    @Value("${banner.image.base-url:/upload/}")
    private String bannerBaseUrl;

    public BannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    @GetMapping
    public ResponseEntity<List<BannerResponse>> getBanners() {
        List<BannerResponse> responses = bannerService.getAllBanners().stream()
                .map(b -> new BannerResponse(
                        b.getId(),
                        bannerBaseUrl + b.getFilename(),
                        b.getAltText(),
                        b.getSortOrder()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    record BannerResponse(Long id, String imageUrl, String altText, Integer sortOrder) {}
}

