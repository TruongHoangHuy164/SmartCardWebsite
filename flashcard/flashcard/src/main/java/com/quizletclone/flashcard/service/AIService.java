package com.quizletclone.flashcard.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AIService {

    @Value("${openrouter.api-key}")
    private String apiKey;

    private final String API_URL = "https://openrouter.ai/api/v1/chat/completions";

    public String ask(String prompt) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", apiKey); // lấy từ application.properties
            headers.set("HTTP-Referer", "http://localhost:8080");

            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);

            JSONArray messages = new JSONArray().put(userMessage);

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "mistralai/mistral-7b-instruct");
            requestBody.put("messages", messages);

            HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject json = new JSONObject(response.getBody());
                return json.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                        .trim();
            } else {
                return "❌ Lỗi API: " + response.getStatusCode() + " - " + response.getBody();
            }
        } catch (Exception e) {
            return "❌ Lỗi ngoại lệ: " + e.getMessage();
        }
    }

    public String generateQuestionFromFlashcard(String term, String definition) {
        String prompt = "Dựa trên flashcard sau, hãy tạo ra một câu hỏi và câu trả lời rõ ràng:\n\n"
                + "Thuật ngữ: " + term + "\n"
                + "Định nghĩa: " + definition + "\n\n"
                + "Trả lời theo định dạng:\nCâu hỏi: ...\nĐáp án: ...";

        return ask(prompt);
    }
}
