package com.carrefour.deliveryapp.repositories;

import com.carrefour.deliveryapp.entities.DeliverySlot;
import com.carrefour.deliveryapp.enums.DeliveryMode;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliverySlotRepository extends JpaRepository<DeliverySlot, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<DeliverySlot> findById(Long id);

    List<DeliverySlot> findByDayAndModeAndMaxReservationsLessThan(DayOfWeek day, DeliveryMode mode, int maxReservations);
}

