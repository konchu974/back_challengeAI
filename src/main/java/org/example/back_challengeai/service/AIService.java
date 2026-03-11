package org.example.back_challengeai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AIService {

    private static final String LM_STUDIO_URL = "http://127.0.0.1:1234/api/v1/chat";
    private static final String MODEL_NAME = "llama-3.2-3b-instruct";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<String> generateChallenges(List<String> interests, List<String> goals, Integer dailyTime) {

        String prompt = String.format(
                "Génère exactement 3 micro-défis pour une personne qui :\n" +
                        "- Aime : %s\n" +
                        "- Objectifs : %s\n" +
                        "- Temps disponible : %d minutes par défi\n\n" +
                        "Format : Un défi par ligne, pas de numéros, pas d'introduction.",
                String.join(", ", interests),
                String.join(", ", goals),
                dailyTime
        );

        String jsonResponse = callLMStudio(prompt);

        try {
            Map<String, Object> parsed = objectMapper.readValue(jsonResponse, Map.class);
            List<Map<String, String>> challenges = (List<Map<String, String>>) parsed.get("challenges");

            return challenges.stream()
                    .map(challenge -> challenge.get("text"))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            return Arrays.stream(jsonResponse.split("\n"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty() && !s.toLowerCase().contains("voici"))
                    .limit(3)
                    .collect(Collectors.toList());
        }
    }

    private String callLMStudio(String userPrompt) {

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL_NAME);
        requestBody.put("input", userPrompt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            Map response = restTemplate.postForObject(LM_STUDIO_URL, request, Map.class);

            System.out.println("=== RÉPONSE LM STUDIO ===");
            System.out.println(response);
            System.out.println("========================");

            if (response.containsKey("output")) {
                List<Map> output = (List<Map>) response.get("output");
                if (output != null && !output.isEmpty()) {
                    Map firstOutput = output.get(0);
                    String content = (String) firstOutput.get("content");
                    return content;
                }
            }

            throw new RuntimeException("Format de réponse inconnu: " + response);

        } catch (Exception e) {
            throw new RuntimeException("Erreur LM Studio: " + e.getMessage());
        }
    }
}