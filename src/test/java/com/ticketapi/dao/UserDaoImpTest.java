//package com.ticketapi.dao;
//
//import com.ticketapi.model.User;
//import com.ticketapi.util.UserQueries;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.dao.DataAccessException;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.PreparedStatementCreator;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.jdbc.support.GeneratedKeyHolder;
//import org.springframework.jdbc.support.KeyHolder;
//
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
//import java.util.Collections;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//class UserDaoImplTest {
//
//    @Mock
//    private JdbcTemplate jdbcTemplate;
//
//    @InjectMocks
//    private UserDaoImpl userDao;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void createUser_Success() {
//        User user = new User("testuser", "test@example.com", "password");
//        when(jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class)))
//                .thenAnswer(invocation -> {
//                    KeyHolder keyHolder = invocation.getArgument(1);
//                    keyHolder.getKeyList().add(Collections.singletonMap("id", 1L));
//                    return 1;
//                });
//
//        User result = userDao.createUser(user);
//
//        assertNotNull(result);
//        assertEquals(1L, result.getId());
//        verify(jdbcTemplate).update(any(PreparedStatementCreator.class), any(KeyHolder.class));
//    }
//
//    @Test
//    void createUser_Failure() {
//        User user = new User("testuser", "test@example.com", "password");
//        when(jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class)))
//                .thenThrow(new DataAccessException("Database error") {});
//
//        User result = userDao.createUser(user);
//
//        assertNotNull(result);
//        assertNull(result.getId());
//        verify(jdbcTemplate).update(any(PreparedStatementCreator.class), any(KeyHolder.class));
//    }
//
//    @Test
//    void getUserById_Success() {
//        Long userId = 1L;
//        User expectedUser = new User(userId, "testuser", "test@example.com", "password", LocalDateTime.now(), LocalDateTime.now(), true);
//        when(jdbcTemplate.queryForObject(eq(UserQueries.GET_USER_BY_ID.getQuery()), eq(new Object[]{userId}), any(RowMapper.class)))
//                .thenReturn(expectedUser);
//
//        User result = userDao.getUserById(userId);
//
//        assertNotNull(result);
//        assertEquals(userId, result.getId());
//        assertEquals(expectedUser.getUsername(), result.getUsername());
//        verify(jdbcTemplate).queryForObject(eq(UserQueries.GET_USER_BY_ID.getQuery()), eq(new Object[]{userId}), any(RowMapper.class));
//    }
//
//    @Test
//    void getUserById_NotFound() {
//        Long userId = 1L;
//        when(jdbcTemplate.queryForObject(eq(UserQueries.GET_USER_BY_ID.getQuery()), eq(new Object[]{userId}), any(RowMapper.class)))
//                .thenThrow(new DataAccessException("User not found") {});
//
//        User result = userDao.getUserById(userId);
//
//        assertNotNull(result);
//        assertNull(result.getId());
//        verify(jdbcTemplate).queryForObject(eq(UserQueries.GET_USER_BY_ID.getQuery()), eq(new Object[]{userId}), any(RowMapper.class));
//    }
//
//    @Test
//    void getUserByUsername_Success() {
//        String username = "testuser";
//        User expectedUser = new User(1L, username, "test@example.com", "password", LocalDateTime.now(), LocalDateTime.now(), true);
//        when(jdbcTemplate.queryForObject(eq(UserQueries.GET_USER_BY_USERNAME.getQuery()), eq(new Object[]{username}), any(RowMapper.class)))
//                .thenReturn(expectedUser);
//
//        User result = userDao.getUserByUsername(username);
//
//        assertNotNull(result);
//        assertEquals(username, result.getUsername());
//        verify(jdbcTemplate).queryForObject(eq(UserQueries.GET_USER_BY_USERNAME.getQuery()), eq(new Object[]{username}), any(RowMapper.class));
//    }
//
//    @Test
//    void getUserByUsername_NotFound() {
//        String username = "testuser";
//        when(jdbcTemplate.queryForObject(eq(UserQueries.GET_USER_BY_USERNAME.getQuery()), eq(new Object[]{username}), any(RowMapper.class)))
//                .thenThrow(new DataAccessException("User not found") {});
//
//        User result = userDao.getUserByUsername(username);
//
//        assertNotNull(result);
//        assertNull(result.getId());
//        verify(jdbcTemplate).queryForObject(eq(UserQueries.GET_USER_BY_USERNAME.getQuery()), eq(new Object[]{username}), any(RowMapper.class));
//    }
//
//    @Test
//    void updateUser_Success() {
//        User user = new User(1L, "testuser", "test@example.com", "password", LocalDateTime.now(), LocalDateTime.now(), true);
//        when(jdbcTemplate.update(eq(UserQueries.UPDATE_USER.getQuery()), any(), any(), any(), eq(user.getId())))
//                .thenReturn(1);
//
//        userDao.updateUser(user);
//
//        verify(jdbcTemplate).update(eq(UserQueries.UPDATE_USER.getQuery()), any(), any(), any(), eq(user.getId()));
//    }
//
//    @Test
//    void updateUser_NoRowsAffected() {
//        User user = new User(1L, "testuser", "test@example.com", "password", LocalDateTime.now(), LocalDateTime.now(), true);
//        when(jdbcTemplate.update(eq(UserQueries.UPDATE_USER.getQuery()), any(), any(), any(), eq(user.getId())))
//                .thenReturn(0);
//
//        userDao.updateUser(user);
//
//        verify(jdbcTemplate).update(eq(UserQueries.UPDATE_USER.getQuery()), any(), any(), any(), eq(user.getId()));
//    }
//
//    @Test
//    void updateUser_Failure() {
//        User user = new User(1L, "testuser", "test@example.com", "password", LocalDateTime.now(), LocalDateTime.now(), true);
//        when(jdbcTemplate.update(eq(UserQueries.UPDATE_USER.getQuery()), any(), any(), any(), eq(user.getId())))
//                .thenThrow(new DataAccessException("Database error") {});
//
//        userDao.updateUser(user);
//
//        verify(jdbcTemplate).update(eq(UserQueries.UPDATE_USER.getQuery()), any(), any(), any(), eq(user.getId()));
//    }
//
//    @Test
//    void deleteUser_Success() {
//        Long userId = 1L;
//        when(jdbcTemplate.update(eq(UserQueries.DELETE_USER.getQuery()), eq(userId)))
//                .thenReturn(1);
//
//        userDao.deleteUser(userId);
//
//        verify(jdbcTemplate).update(eq(UserQueries.DELETE_USER.getQuery()), eq(userId));
//    }
//
//    @Test
//    void deleteUser_NoRowsAffected() {
//        Long userId = 1L;
//        when(jdbcTemplate.update(eq(UserQueries.DELETE_USER.getQuery()), eq(userId)))
//                .thenReturn(0);
//
//        userDao.deleteUser(userId);
//
//        verify(jdbcTemplate).update(eq(UserQueries.DELETE_USER.getQuery()), eq(userId));
//    }
//
//    @Test
//    void deleteUser_Failure() {
//        Long userId = 1L;
//        when(jdbcTemplate.update(eq(UserQueries.DELETE_USER.getQuery()), eq(userId)))
//                .thenThrow(new DataAccessException("Database error") {});
//
//        userDao.deleteUser(userId);
//
//        verify(jdbcTemplate).update(eq(UserQueries.DELETE_USER.getQuery()), eq(userId));
//    }
//}