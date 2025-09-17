package com.myboot.detail.model;

import com.myboot.intro.model.Restaurant;
import com.myboot.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "reservation", uniqueConstraints = @UniqueConstraint(columnNames = { "restaurant_id", "reserved_date",
        "reserved_time" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** FK */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    /** 예약자 입력 */
    @Column(name = "customer_name", nullable = false)
    private String customerName;
    @Column(name = "customer_phone", nullable = false)
    private String customerPhone;

    /** 예약 일시 */
    @Column(name = "reserved_date", nullable = false)
    private LocalDate reservedDate; // yyyy-MM-dd
    @Column(name = "reserved_time", nullable = false)
    private String reservedTime; // HH:mm

    @Column(name = "status", length = 20)
    private String status; // CONFIRMED 등
}
