//package com.ticketapi.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.ticketapi.model.User;
//import com.ticketapi.util.JwtUtil;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc(addFilters = false)
//@ActiveProfiles("test")
//@Transactional
//public class UserControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    private String jwtToken;
//
//    private static final Logger logger = LoggerFactory.getLogger(UserControllerIntegrationTest.class);
//
//    @BeforeEach
//    public void setup() throws Exception {
//        User user = new User();
//        user.setUsername("testuser" + System.currentTimeMillis());
//        user.setPassword("password123");
//        user.setEmail("testuser" + System.currentTimeMillis() + "@example.com");
//
//        logger.debug("Attempting to register user: {}", user.getUsername());
//
//        // Register the user
//        MvcResult registerResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(user)))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String registerResponse = registerResult.getResponse().getContentAsString();
//        logger.debug("Register Response: {}", registerResponse);
//
//        // Extract the registered user from the response
//        User registeredUser = objectMapper.readValue(registerResponse, User.class);
//        logger.debug("Registered User: {}", registeredUser);
//
//        logger.debug("Attempting to login user: {}", user.getUsername());
//
//        // Login to get the JWT token
//        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(user)))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String loginResponse = loginResult.getResponse().getContentAsString();
//        logger.debug("Login Response: {}", loginResponse);
//
//        jwtToken = objectMapper.readTree(loginResponse).get("token").asText();
//        logger.debug("Extracted JWT Token: {}", jwtToken);
//
//        assertNotNull(jwtToken, "JWT token should not be null");
//        assertFalse(jwtToken.isEmpty(), "JWT token should not be empty");
//    }
//
//    @Test
//    public void testGetUserProfile() throws Exception {
//        logger.debug("Testing user profile with JWT Token: {}", jwtToken);
//
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/profile")
//                        .header("Authorization", "Bearer " + jwtToken))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.username").exists())
//                .andReturn();
//
//        logger.debug("Response Body: {}", result.getResponse().getContentAsString());
//        if (result.getResponse().getStatus() != 200) {
//            logger.error("Test failed. Response body: {}", result.getResponse().getContentAsString());
//        }
//    }
//    @Test
//    public void testGetUserProfileUnauthorized() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/profile"))
//                .andExpect(status().isUnauthorized());
//    }
//}