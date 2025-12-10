package com.diaconn_mall.website.service;

import com.diaconn_mall.website.entity.Banner;
import com.diaconn_mall.website.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;

    public List<Banner> getBanners() {
        return bannerRepository.findAll();
    }
}

