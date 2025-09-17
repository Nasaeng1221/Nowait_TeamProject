package com.myboot.intro.service;

import com.myboot.intro.model.Restaurant;
import com.myboot.intro.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // ✅ 생성자 주입 자동 생성
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository; // ✅ 빠져있던 필드 추가

    @Override
    public Page<Restaurant> findByRegion(String region, Pageable pageable) {
        return restaurantRepository.searchByRegion(region, pageable);
    }

    @Override
    public Optional<Restaurant> findById(Long id) {
        return restaurantRepository.findById(id);
    }

    @Override
    public List<Restaurant> getRandomRestaurants(int count) {
        return restaurantRepository.findRandomRestaurants(count);
    }
}
