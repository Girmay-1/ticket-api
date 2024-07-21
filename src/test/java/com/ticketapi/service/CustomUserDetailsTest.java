//package com.ticketapi.service;
//
//
//import com.ticketapi.model.User;
//import com.ticketapi.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class CustomUserDetailsServiceTest {
//
//    @Mock
//    private UserService userService;
//
//    @InjectMocks
//    private CustomUserDetailsService customUserDetailsService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void loadUserByUsername_UserExists_ReturnsUserDetails() {
//        String username = "testuser";
//        User user = new User();
//        user.setUsername(username);
//        user.setPasswordHash("hashedpassword");
//
//        when(userService.getUserByUsername(username)).thenReturn(user);
//
//        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
//
//        assertNotNull(userDetails);
//        assertEquals(username, userDetails.getUsername());
//        assertEquals("hashedpassword", userDetails.getPassword());
//        assertTrue(userDetails.getAuthorities().stream()
//                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
//    }
//
//    @Test
//    void loadUserByUsername_UserNotFound_ThrowsException() {
//        String username = "nonexistentuser";
//
//        when(userService.getUserByUsername(username)).thenReturn(null);
//
//        assertThrows(UsernameNotFoundException.class, () -> {
//            customUserDetailsService.loadUserByUsername(username);
//        });
//    }
//}