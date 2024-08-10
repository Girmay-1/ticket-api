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
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
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
//    @BeforeEach
//    public void setup() throws Exception {
//        User user = new User();
//        user.setUsername("testuser" + System.currentTimeMillis());
//        user.setPassword("password123");
//        user.setEmail("testuser" + System.currentTimeMillis() + "@example.com");
//
//        // Register the user
//        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(user)))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        // Extract the registered user from the response
//        User registeredUser = objectMapper.readValue(registerResult.getResponse().getContentAsString(), User.class);
//
//        // Login to get the JWT token
//        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(user))) // Use the original user object with the plain password
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String loginResponse = loginResult.getResponse().getContentAsString();
//        jwtToken = objectMapper.readTree(loginResponse).get("token").asText();
//    }
//
//    @Test
//    public void testGetUserProfile() throws Exception {
//        mockMvc.perform(get("/api/users/profile")
//                        .header("Authorization", "Bearer " + jwtToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.username").exists());
//    }
//
//    @Test
//    public void testGetUserProfileUnauthorized() throws Exception {
//        mockMvc.perform(get("/api/users/profile"))
//                .andExpect(status().isUnauthorized());
//    }
//}