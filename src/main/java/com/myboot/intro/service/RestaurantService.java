package com.myboot.intro.service;

import com.myboot.intro.model.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RestaurantService {
    Page<Restaurant> findByRegion(String region, Pageable pageable);

    Optional<Restaurant> findById(Long id); // 상세 조회

    List<Restaurant> getRandomRestaurants(int count); // ✅ 랜덤 3개 추출
}