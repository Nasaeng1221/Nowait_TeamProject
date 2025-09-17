package com.myboot.detail.controller;

import com.myboot.detail.model.Reservation;
import com.myboot.detail.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/make")
    public Map<String, Object> makeReservation(@RequestParam Long restaurantId, @RequestParam Long memberId,
            @RequestParam String customerName, @RequestParam String customerPhone, @RequestParam String date,
            @RequestParam String time) {
        Reservation reservation = reservationService.makeReservation(restaurantId, memberId, customerName,
                customerPhone, date, time);

        return Map.of("result", "OK", "reservationId", reservation.getId());
    }
}
