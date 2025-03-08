package com.carrefour.deliveryapp.security;

import com.carrefour.deliveryapp.security.models.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private static final String TOKEN_PREFIX = "Bearer ";
    private final AuthenticationManager authenticationManager;
    private final JwtHandler jwtHandler;

    public AuthenticationService(AuthenticationManager authenticationManager, JwtHandler jwtHandler) {
        this.authenticationManager = authenticationManager;
        this.jwtHandler = jwtHandler;
    }

    public String authenticateUser(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return TOKEN_PREFIX + jwtHandler.generateToken(userDetails.getUsername());
    }
}
