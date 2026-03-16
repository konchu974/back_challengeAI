package org.example.back_challengeai.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.back_challengeai.dto.RegisterRequest;
import org.example.back_challengeai.entity.NotificationType;
import org.example.back_challengeai.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationService notificationService;

    private String token;
    private UUID userId;

    @BeforeEach
    void setUp() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("notif@test.com");
        request.setPassword("password123");
        request.setPseudo("NotifUser");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        token = body.get("token").asText();
        userId = UUID.fromString(body.get("id").asText());

        // Créer quelques notifications pour les tests
        notificationService.createNotification(userId, "Test reminder", NotificationType.REMINDER);
        notificationService.createNotification(userId, "Test achievement", NotificationType.ACHIEVEMENT);
    }

    @Test
    void getUserNotifications_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/notifications")
                        .param("userId", userId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getUnreadNotifications_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/notifications/unread")
                        .param("userId", userId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getUnreadCount_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/notifications/unread/count")
                        .param("userId", userId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").exists());
    }

    @Test
    void markAsRead_ShouldReturn200() throws Exception {
        // Récupérer une notification existante
        MvcResult result = mockMvc.perform(get("/api/notifications")
                        .param("userId", userId.toString())
                        .header("Authorization", "Bearer " + token))
                .andReturn();

        JsonNode notifications = objectMapper.readTree(result.getResponse().getContentAsString());
        String notifId = notifications.get(0).get("id").asText();

        mockMvc.perform(post("/api/notifications/" + notifId + "/read")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
