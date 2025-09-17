package com.myboot.detail.repository;

import com.myboot.detail.model.Reservation;
import com.myboot.intro.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // 특정 가게 전체
    List<Reservation> findByRestaurant(Restaurant restaurant);

    // 특정 가게 + 날짜
    List<Reservation> findByRestaurantAndReservedDate(Restaurant restaurant, LocalDate reservedDate);

    // 특정 가게 + 날짜 + 시간 존재여부
    boolean existsByRestaurantAndReservedDateAndReservedTime(Restaurant restaurant, LocalDate reservedDate,
            String reservedTime);

    boolean existsByRestaurantAndMember_Id(Restaurant restaurant, Long memberId);
}
