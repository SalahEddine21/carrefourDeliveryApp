package com.carrefour.deliveryapp.utils;

import com.carrefour.deliveryapp.entities.Customer;
import com.carrefour.deliveryapp.entities.DeliverySlot;
import com.carrefour.deliveryapp.entities.Product;
import com.carrefour.deliveryapp.enums.DeliveryMode;
import com.carrefour.deliveryapp.repositories.CustomerRepository;
import com.carrefour.deliveryapp.repositories.DeliverySlotRepository;
import com.carrefour.deliveryapp.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class DataLoader implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final DeliverySlotRepository deliverySlotRepository;
    private final ProductRepository productRepository;
    private final DataResetService dataResetService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        dataResetService.resetDBDATA();

        Customer customer = new Customer();
        customer.setFirstName("Salaheddine");
        customer.setLastName("LAHMAM");
        customer.setEmail("salahedd.lahmam@gmail.com");
        customer.setPassword(passwordEncoder.encode("sla21"));
        customerRepository.save(customer);

        Product product1 = new Product();
        product1.setCode("P001");
        product1.setName("Wireless Mouse");
        product1.setPrice(25.99);
        product1.setStockQuantity(100);

        Product product2 = new Product();
        product2.setCode("P002");
        product2.setName("Mechanical Keyboard");
        product2.setPrice(79.99);
        product2.setStockQuantity(50);

        Product product3 = new Product();
        product3.setCode("P003");
        product3.setName("USB-C Docking Station");
        product3.setPrice(129.99);
        product3.setStockQuantity(30);

        productRepository.saveAll(Arrays.asList(product1, product2, product3));

        Arrays.stream(DayOfWeek.values()).forEach(day -> {
            final List<DeliverySlot> daySlots = new ArrayList<>();
            LocalTime startTime = LocalTime.of(10, 0);
            for (DeliveryMode mode : DeliveryMode.values()) {
                DeliverySlot slot = new DeliverySlot();
                slot.setDay(day);
                slot.setMode(mode);
                slot.setStartTime(startTime);
                slot.setEndTime(startTime.plusHours(2));
                slot.setMaxReservations(5);
                startTime = startTime.plusHours(2);
                daySlots.add(slot);
            }
            deliverySlotRepository.saveAll(daySlots);
        });
    }
}
