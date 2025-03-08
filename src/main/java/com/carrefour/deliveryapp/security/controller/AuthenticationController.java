package com.carrefour.deliveryapp.security.controller;

import com.carrefour.deliveryapp.security.AuthenticationService;
import com.carrefour.deliveryapp.security.models.AuthenticationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/token")
    public ResponseEntity<String> authenticate(@RequestBody AuthenticationRequest request) {
        final String token = authenticationService.authenticateUser(request.getEmail(), request.getPassword());
        return ResponseEntity.ok().body(token);
    }
}
