package com.carrefour.deliveryapp.repositories;

import com.carrefour.deliveryapp.entities.Customer;
import com.carrefour.deliveryapp.entities.Delivery;
import com.carrefour.deliveryapp.entities.DeliverySlot;
import com.carrefour.deliveryapp.enums.DeliveryMode;
import com.carrefour.deliveryapp.enums.DeliveryStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class DeliveryRepositoryTest {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private DeliverySlotRepository deliverySlotRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        DeliverySlot slot = new DeliverySlot();
        slot.setWeekday(DayOfWeek.MONDAY);
        slot.setStartTime(LocalTime.of(10, 0));
        slot.setEndTime(LocalTime.of(12, 0));
        slot.setMaxReservations(5);
        slot.setMode(DeliveryMode.DRIVE);
        deliverySlotRepository.save(slot);

        Customer customer = new Customer();
        customer.setEmail("salahedd.lahmam@gmail.com");
        customerRepository.save(customer);

        Delivery delivery1 = new Delivery();
        delivery1.setCustomer(customer);
        delivery1.setDeliverySlot(slot);
        delivery1.setStatus(DeliveryStatusEnum.READY);
        delivery1.setDeliveryAddress("Casa oulfa");

        Delivery delivery2 = new Delivery();
        delivery2.setCustomer(customer);
        delivery2.setDeliverySlot(slot);
        delivery2.setStatus(DeliveryStatusEnum.DELIVERED);
        delivery2.setDeliveryAddress("Casa oulfa");

        Delivery delivery3 = new Delivery();
        delivery3.setCustomer(customer);
        delivery3.setDeliverySlot(slot);
        delivery3.setStatus(DeliveryStatusEnum.ACCEPTED);
        delivery3.setDeliveryAddress("Casa oulfa");

        deliveryRepository.saveAll(Arrays.asList(delivery1, delivery2, delivery3));
    }

    @Test
    void testCountByDeliverySlotId() {
        long count = deliveryRepository.countByDeliverySlotId(1L);
        assertEquals(3, count);
    }
}
