package com.diaconn_mall.website.repository;

import com.diaconn_mall.website.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // nm 컬럼에 검색어가 포함된 상품 조회 (Like 쿼리)
    List<Product> findByNmContaining(String nm);
}
