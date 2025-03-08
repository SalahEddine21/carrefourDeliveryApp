package com.carrefour.deliveryapp.dtos;

import com.carrefour.deliveryapp.enums.DeliveryMode;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class DeliverySlotDTO {
    private Long id;
    private DeliveryMode mode;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer maxReservations;
}
