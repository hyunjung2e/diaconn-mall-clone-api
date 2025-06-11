package com.diaconn_mall.website.repository;

import com.diaconn_mall.website.entity.Main;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MainRepository extends JpaRepository<Main, Long> {
    List<Main> findAllByOrderBySortOrderAsc();
}
