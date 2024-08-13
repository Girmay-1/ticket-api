package com.ticketapi.controller;

import com.ticketapi.model.User;
import com.ticketapi.util.JwtUtil;
import com.ticketapi.service.CustomUserDetailsService;
import com.ticketapi.service.UserService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication operations")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    private final static Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthenticationManager authenticationManager,
                          CustomUserDetailsService userDetailsService,
                          UserService userService,
                          JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    @Timed(value = "user.register.request", description = "Time taken to register a user.")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            logger.info("successfully registered user : {}", user);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            logger.error("Exception registering a user:  ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error registering user: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate a user and get JWT token")
    public ResponseEntity<?> loginUser(@RequestBody User loginUser) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Incorrect username or password");
        }

        try {
            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginUser.getUsername());
            final String jwt = jwtUtil.generateToken(userDetails.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("username", userDetails.getUsername());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during login process");
        }
    }
}