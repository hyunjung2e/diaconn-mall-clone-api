package com.diaconn_mall.website.repository;

import com.diaconn_mall.website.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findAllByOrderBySortOrderAsc();
}
