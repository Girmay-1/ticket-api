//package com.ticketapi.service;
//
//import com.ticketapi.dao.UserDao;
//import com.ticketapi.model.User;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.time.LocalDateTime;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//class UserServiceTest {
//
//    @Mock
//    private UserDao userDao;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @InjectMocks
//    private UserService userService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void createUser_Success() {
//        User user = new User("testuser", "test@example.com", "password");
//        User createdUser = new User(1L, "testuser", "test@example.com", "encodedPassword", LocalDateTime.now(), LocalDateTime.now(), true);
//
//        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
//        when(userDao.createUser(any(User.class))).thenReturn(createdUser);
//
//        User result = userService.createUser(user);
//
//        assertNotNull(result);
//        assertEquals(createdUser.getId(), result.getId());
//        verify(passwordEncoder).encode(eq("password"));
//        verify(userDao).createUser(any(User.class));
//    }
//
//    @Test
//    void createUser_Failure() {
//        User user = new User("testuser", "test@example.com", "password");
//        User failedUser = new User();
//
//        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
//        when(userDao.createUser(any(User.class))).thenReturn(failedUser);
//
//        User result = userService.createUser(user);
//
//        assertNotNull(result);
//        assertNull(result.getId());
//        verify(passwordEncoder).encode(eq("password"));
//        verify(userDao).createUser(any(User.class));
//    }
//
//    @Test
//    void getUserById_Success() {
//        Long userId = 1L;
//        User expectedUser = new User(userId, "testuser", "test@example.com", "encodedPassword", LocalDateTime.now(), LocalDateTime.now(), true);
//
//        when(userDao.getUserById(userId)).thenReturn(expectedUser);
//
//        User result = userService.getUserById(userId);
//
//        assertNotNull(result);
//        assertEquals(expectedUser.getId(), result.getId());
//        assertEquals(expectedUser.getUsername(), result.getUsername());
//        verify(userDao).getUserById(userId);
//    }
//
//    @Test
//    void getUserById_Failure() {
//        Long userId = 1L;
//        User emptyUser = new User();
//
//        when(userDao.getUserById(userId)).thenReturn(emptyUser);
//
//        User result = userService.getUserById(userId);
//
//        assertNotNull(result);
//        assertNull(result.getId());
//        verify(userDao).getUserById(userId);
//    }
//
//    @Test
//    void getUserByUsername_Success() {
//        String username = "testuser";
//        User expectedUser = new User(1L, username, "test@example.com", "encodedPassword", LocalDateTime.now(), LocalDateTime.now(), true);
//
//        when(userDao.getUserByUsername(username)).thenReturn(expectedUser);
//
//        User result = userService.getUserByUsername(username);
//
//        assertNotNull(result);
//        assertEquals(expectedUser.getId(), result.getId());
//        assertEquals(expectedUser.getUsername(), result.getUsername());
//        verify(userDao).getUserByUsername(username);
//    }
//
//    @Test
//    void getUserByUsername_Failure() {
//        String username = "testuser";
//        User emptyUser = new User();
//
//        when(userDao.getUserByUsername(username)).thenReturn(emptyUser);
//
//        User result = userService.getUserByUsername(username);
//
//        assertNotNull(result);
//        assertNull(result.getId());
//        verify(userDao).getUserByUsername(username);
//    }
//
//    @Test
//    void updateUser() {
//        User user = new User(1L, "testuser", "test@example.com", "encodedPassword", LocalDateTime.now(), LocalDateTime.now(), true);
//
//        userService.updateUser(user);
//
//        verify(userDao).updateUser(user);
//    }
//
//    @Test
//    void deleteUser() {
//        Long userId = 1L;
//
//        userService.deleteUser(userId);
//
//        verify(userDao).deleteUser(userId);
//    }
//}