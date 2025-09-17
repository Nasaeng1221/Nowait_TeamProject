package com.myboot.intro.model;

import com.myboot.detail.model.Reservation;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "restaurant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String si;
    private String gu;
    private String dong;
    private String name;
    private String image;
    private String phone;
    private String address;

    @Column(name = "main_menu")
    private String mainMenu;

    @Column(name = "open_hours")
    private String openHours;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations;
}
