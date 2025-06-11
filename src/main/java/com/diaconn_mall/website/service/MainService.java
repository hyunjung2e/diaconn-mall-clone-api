package com.diaconn_mall.website.service;

import com.diaconn_mall.website.entity.Main;
import com.diaconn_mall.website.repository.MainRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MainService {

    private final MainRepository bannerRepository;

    public MainService(MainRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    public List<Main> getAllBanners() {
        List<Main> banners = bannerRepository.findAll();
        System.out.println("배너 리스트 크기: " + banners.size());  // 디버깅용 로그
        return banners;
    }
}
