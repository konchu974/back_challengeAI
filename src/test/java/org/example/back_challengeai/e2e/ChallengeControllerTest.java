package org.example.back_challengeai.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.back_challengeai.dto.RegisterRequest;
import org.example.back_challengeai.service.AIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ChallengeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AIService aiService;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        when(aiService.generateChallenges(any(), any(), anyInt()))
                .thenReturn(List.of("Défi 1 test", "Défi 2 test", "Défi 3 test"));

        RegisterRequest request = new RegisterRequest();
        request.setEmail("challenge@test.com");
        request.setPassword("password123");
        request.setPseudo("ChallengeUser");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        token = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").asText();
    }

    @Test
    void getTodayChallenges_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/challenges/today")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void completeChallenge_ShouldReturn200() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/challenges/today")
                        .header("Authorization", "Bearer " + token))
                .andReturn();

        JsonNode challenges = objectMapper.readTree(result.getResponse().getContentAsString());
        String challengeId = challenges.get(0).get("id").asText();

        mockMvc.perform(post("/api/challenges/" + challengeId + "/complete")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void skipChallenge_ShouldReturn200() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/challenges/today")
                        .header("Authorization", "Bearer " + token))
                .andReturn();

        JsonNode challenges = objectMapper.readTree(result.getResponse().getContentAsString());
        String challengeId = challenges.get(0).get("id").asText();

        mockMvc.perform(post("/api/challenges/" + challengeId + "/skip")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SKIPPED"));
    }
}
