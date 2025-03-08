package com.carrefour.deliveryapp.repositories;

import com.carrefour.deliveryapp.entities.DeliverySlot;
import com.carrefour.deliveryapp.enums.DeliveryMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class DeliverySlotRepositoryTest {

    @Autowired
    private DeliverySlotRepository deliverySlotRepository;

    @BeforeEach
    void setUp() {
        DeliverySlot slot = new DeliverySlot();
        slot.setWeekday(DayOfWeek.MONDAY);
        slot.setStartTime(LocalTime.of(10, 0));
        slot.setEndTime(LocalTime.of(12, 0));
        slot.setMaxReservations(5);
        slot.setMode(DeliveryMode.DRIVE);
        deliverySlotRepository.save(slot);
    }

    @Test
    void testFindByIdWithPessimisticWriteLock(){
        Optional<DeliverySlot> deliverySlot = deliverySlotRepository.findById(1L);
        assertTrue(deliverySlot.isPresent());
        assertEquals(1, deliverySlot.get().getId());
    }

    @Test
    void testFindByWeekdayAndModeAndMaxReservationsLessThan(){
        List<DeliverySlot> slots = deliverySlotRepository.
                findByWeekdayAndMode(DayOfWeek.MONDAY, DeliveryMode.DRIVE);
        assertFalse(slots.isEmpty());
        assertEquals(1, slots.size());
    }
}
