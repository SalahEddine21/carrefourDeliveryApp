package com.carrefour.deliveryapp.entities;

import com.carrefour.deliveryapp.enums.DeliveryMode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class DeliverySlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DeliveryMode mode;

    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer maxReservations;

    @OneToMany(mappedBy = "deliverySlot")
    private List<Delivery> deliveries;

}
