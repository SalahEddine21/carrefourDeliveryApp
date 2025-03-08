package com.carrefour.deliveryapp.repositories;

import com.carrefour.deliveryapp.entities.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testFindByEmail() {
        Customer customer = new Customer();
        customer.setEmail("salahedd.lahmam@gmail.com");
        customerRepository.save(customer);

        Optional<Customer> foundCustomer = customerRepository.findByEmail("salahedd.lahmam@gmail.com");
        assertTrue(foundCustomer.isPresent());
        assertEquals("salahedd.lahmam@gmail.com", foundCustomer.get().getEmail());
    }
}