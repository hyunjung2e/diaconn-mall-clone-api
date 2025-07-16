package com.diaconn_mall.website.repository;

import com.diaconn_mall.website.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 로그인 또는 사용자 조회
    Optional<User> findByEmail(String email);

    // 이메일 중복 검사
    boolean existsByEmail(String email);
}
