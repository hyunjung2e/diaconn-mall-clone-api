package com.diaconn_mall.website.service;

import com.diaconn_mall.website.entity.Banner;
import com.diaconn_mall.website.repository.BannerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerService {

    private final BannerRepository bannerRepository;

    public BannerService(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    public List<Banner> getAllBanners() {
        List<Banner> banners = bannerRepository.findAll();
        System.out.println("배너 리스트 크기: " + banners.size());  // 디버깅용 로그
        return banners;
    }
}
