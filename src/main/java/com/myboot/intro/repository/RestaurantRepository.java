package com.myboot.intro.repository;

import com.myboot.intro.model.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    // si, gu, dong, name 중 하나라도 region 키워드가 포함되면 검색
    @Query("SELECT r FROM Restaurant r " + "WHERE r.si LIKE CONCAT('%', :region, '%') "
            + "OR r.gu LIKE CONCAT('%', :region, '%') " + "OR r.dong LIKE CONCAT('%', :region, '%') "
            + "OR r.name LIKE CONCAT('%', :region, '%')")
    Page<Restaurant> searchByRegion(@Param("region") String region, Pageable pageable);

    // 랜덤으로 N개 뽑기
    @Query(value = "SELECT * FROM restaurant ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<Restaurant> findRandomRestaurants(@Param("count") int count);

}
