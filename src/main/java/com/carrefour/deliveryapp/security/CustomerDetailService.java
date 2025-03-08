package com.carrefour.deliveryapp.security;

import com.carrefour.deliveryapp.entities.Customer;
import com.carrefour.deliveryapp.repositories.CustomerRepository;
import com.carrefour.deliveryapp.security.models.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerDetailService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    public CustomerDetailService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found with email: " + email));
        return CustomUserDetails.builder()
                .username(customer.getEmail())
                .password(customer.getPassword())
                .build();
    }

}
