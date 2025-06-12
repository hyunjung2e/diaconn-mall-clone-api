package com.diaconn_mall.website.controller;
import com.diaconn_mall.website.service.MainService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/main")
public class MainController {

    private final MainService mainService;

    @Value("${banner.image.base-url:/upload/}")
    private String mainBaseUrl;

    public MainController(MainService mainService) {
        this.mainService = mainService;
    }

    @GetMapping
    public ResponseEntity<List<MainResponse>> getBanners() {
        List<MainResponse> responses = mainService.getAllBanners().stream()
                .map(b -> new MainResponse(
                        b.getId(),
                        "/api/images/" + b.getFilename(),  // 이미지 접근 경로 변경
                        b.getAltText(),
                        b.getSortOrder()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    record MainResponse(Long id, String imageUrl, String altText, Integer sortOrder) {}
}

