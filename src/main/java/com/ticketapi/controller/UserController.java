package com.ticketapi.controller;

import com.ticketapi.model.User;
import com.ticketapi.service.CustomUserDetailsService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User operations")
@SecurityRequirement(name = "jwt_auth")
public class UserController {
    private final static Logger logger = LoggerFactory.getLogger(UserController.class);
    private final CustomUserDetailsService userDetailsService;

    public UserController(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/profile")
    @Operation(summary = "Get the profile of the authenticated user")
    @Timed(value = "user.profile.request", description = "Time taken to return user profile")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        if (authentication == null) {
            logger.warn("unauthorized access attempt to user prifle");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not authenticated");
        }

        try {
            String username = authentication.getName();
            logger.info("Fetching user profile for user: {}", username);
            User user = (User) userDetailsService.loadUserByUsername(username);
            return ResponseEntity.ok(user);
        } catch (UsernameNotFoundException e) {
            logger.error("User not found: {}", authentication.getName(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        } catch (Exception e) {
            logger.error("Error retrieving user profile for user: {}", authentication.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving user profile");
        }
    }
}