//package com.ticketapi.controller;
//
//import com.ticketapi.model.User;
//import com.ticketapi.service.UserService;
//import com.ticketapi.util.JwtUtil;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//class UserControllerTest {
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private JwtUtil jwtUtil;
//
//    @Mock
//    private AuthenticationManager authenticationManager;
//
//    @InjectMocks
//    private UserController userController;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void registerUser_ShouldReturnCreatedUser() {
//        User user = new User("testuser", "test@example.com", "password");
//        when(userService.createUser(any(User.class))).thenReturn(user);
//
//        ResponseEntity<?> response = userController.registerUser(user);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertTrue(response.getBody() instanceof User);
//        assertEquals(user.getUsername(), ((User) response.getBody()).getUsername());
//        verify(userService).createUser(any(User.class));
//    }
//
//    @Test
//    void registerUser_ShouldReturnBadRequestOnException() {
//        User user = new User("testuser", "test@example.com", "password");
//        when(userService.createUser(any(User.class))).thenThrow(new RuntimeException("Test exception"));
//
//        ResponseEntity<?> response = userController.registerUser(user);
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertTrue(response.getBody() instanceof String);
//        assertTrue(((String) response.getBody()).contains("Test exception"));
//        verify(userService).createUser(any(User.class));
//    }
//
//    @Test
//    void loginUser_ShouldReturnTokenOnSuccess() {
//        User loginUser = new User("testuser", "test@example.com", "password");
//        Authentication auth = mock(Authentication.class);
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
//        when(jwtUtil.generateToken(anyString())).thenReturn("testToken");
//
//        ResponseEntity<?> response = userController.loginUser(loginUser);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertTrue(response.getBody() instanceof Map);
//        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
//        assertEquals("testToken", responseBody.get("token"));
//        assertEquals(loginUser.getUsername(), responseBody.get("username"));
//        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
//        verify(jwtUtil).generateToken(loginUser.getUsername());
//        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
//    }
//
//    @Test
//    void loginUser_ShouldReturnUnauthorizedOnFailure() {
//        User loginUser = new User("testuser", "test@example.com", "password");
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenThrow(new RuntimeException("Authentication failed"));
//
//        ResponseEntity<?> response = userController.loginUser(loginUser);
//
//        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//        assertTrue(response.getBody() instanceof String);
//        assertTrue(((String) response.getBody()).contains("Authentication failed"));
//        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
//        verifyNoInteractions(jwtUtil);
//    }
//
//    @Test
//    void getUserProfile_ShouldReturnUserWhenAuthenticated() {
//        Authentication authentication = mock(Authentication.class);
//        when(authentication.isAuthenticated()).thenReturn(true);
//        when(authentication.getName()).thenReturn("testuser");
//        User user = new User("testuser", "test@example.com", "password");
//        when(userService.getUserByUsername("testuser")).thenReturn(user);
//
//        ResponseEntity<?> response = userController.getUserProfile(authentication);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertTrue(response.getBody() instanceof User);
//        assertEquals("testuser", ((User) response.getBody()).getUsername());
//        verify(userService).getUserByUsername("testuser");
//    }
//
//    @Test
//    void getUserProfile_ShouldReturnUnauthorizedWhenNotAuthenticated() {
//        Authentication authentication = mock(Authentication.class);
//        when(authentication.isAuthenticated()).thenReturn(false);
//
//        ResponseEntity<?> response = userController.getUserProfile(authentication);
//
//        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//        assertEquals("User not authenticated", response.getBody());
//        verifyNoInteractions(userService);
//    }
//}