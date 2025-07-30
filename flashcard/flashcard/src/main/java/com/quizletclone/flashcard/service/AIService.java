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
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Base64;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizletclone.flashcard.model.Flashcard;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            headers.set("HTTP-Referer", "http://localhost:8088");

            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);

            JSONArray messages = new JSONArray().put(userMessage);

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "mistralai/mistral-7b-instruct:free");
            requestBody.put("messages", messages); // BỔ SUNG DÒNG NÀY

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

    public List<Flashcard> generateFlashcards(String prompt, int count) {
        List<Flashcard> list = new ArrayList<>();
        String aiPrompt = "Hãy tạo " + count + " từ liên về chủ đề '" + prompt +
                "'. Mỗi flashcard là một đối tượng JSON gồm: " +
                "\"term\" , " +
                "\"definition\" (giải thích ngắn về term ). " +
                "Chỉ trả về JSON dạng: [{\"term\": \"...\", \"definition\": \"...\"}, ...]. Không thêm bất kỳ mô tả nào khác.";

        String aiResult = ask(aiPrompt);

        try {
            ObjectMapper mapper = new ObjectMapper();
            Flashcard[] cards = mapper.readValue(aiResult, Flashcard[].class);
            for (int i = 0; i < Math.min(cards.length, count); i++) {
                list.add(cards[i]);
            }
            for (int i = list.size(); i < count; i++) {
                String term = ask(
                        "Hãy tạo một thuật ngữ (chỉ là một từ hoặc cụm từ ngắn, không kèm giải thích) liên quan đến '"
                                + prompt + "', chỉ trả về thuật ngữ.");
                String definition = ask(
                        "Định nghĩa ngắn gọn cho '" + term + "' về chủ đề '" + prompt + "', chỉ trả về định nghĩa.");
                Flashcard fc = new Flashcard();
                fc.setTerm(term.trim());
                fc.setDefinition(definition.trim());
                list.add(fc);
            }
        } catch (Exception e) {
            e.printStackTrace();
            for (int i = 1; i <= count; i++) {
                String term = ask(
                        "Hãy tạo một thuật ngữ (chỉ là một từ hoặc cụm từ ngắn, không kèm giải thích) liên quan đến '"
                                + prompt + "', chỉ trả về thuật ngữ.");
                String definition = ask(
                        "Định nghĩa ngắn gọn cho '" + term + "' về chủ đề '" + prompt + "', chỉ trả về định nghĩa.");
                Flashcard fc = new Flashcard();
                fc.setTerm(term.trim());
                fc.setDefinition(definition.trim());
                list.add(fc);
            }
        }
        return list;
    }

    public String generateQuestionFromFlashcard(String term, String definition) {
        String prompt = "Dựa trên flashcard sau, hãy tạo ra một câu hỏi và câu trả lời rõ ràng:\n\n"
                + "Thuật ngữ: " + term + "\n"
                + "Định nghĩa: " + definition + "\n\n"
                + "Trả lời theo định dạng:\nCâu hỏi: ...\nĐáp án: ...";

        return ask(prompt);
    }

    public boolean isSensitiveText(String text) {
        String prompt = "Hãy kiểm tra nội dung sau có chứa từ ngữ nhạy cảm, tục tĩu, hoặc vi phạm thuần phong mỹ tục bằng tiếng Việt hoặc tiếng Anh không? Nếu có, chỉ trả lời 'YES', nếu không chỉ trả lời 'NO'. Nội dung: "
                + text;
        String result = ask(prompt);
        return result.trim().toUpperCase().contains("YES");
    }

    public boolean isSensitiveImage(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            return false;
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", apiKey);

            // Convert image to Base64
            String base64Image = Base64.getEncoder().encodeToString(imageFile.getBytes());
            String mimeType = imageFile.getContentType();

            // Create prompt for vision model
            JSONObject textContent = new JSONObject();
            textContent.put("type", "text");
            textContent.put("text",
                    "Does this image contain nudity, violence, hate speech, or other sensitive content? Answer with only YES or NO.");

            JSONObject imageUrlContent = new JSONObject();
            imageUrlContent.put("type", "image_url");
            JSONObject imageUrlData = new JSONObject();
            imageUrlData.put("url", "data:" + mimeType + ";base64," + base64Image);
            imageUrlContent.put("image_url", imageUrlData);

            JSONArray contentArray = new JSONArray();
            contentArray.put(textContent);
            contentArray.put(imageUrlContent);

            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", contentArray);

            JSONArray messages = new JSONArray().put(userMessage);

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "qwen/qwen2.5-vl-72b-instruct:free"); // Using a vision model
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", 10);

            HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject json = new JSONObject(response.getBody());
                String result = json.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                        .trim()
                        .toUpperCase();
                return result.contains("YES");
            }

        } catch (IOException e) {
            // Log error, but don't block user for now
            e.printStackTrace();
            return false;
        } catch (HttpClientErrorException e) {
            // Nếu mã lỗi là 400 và message chứa "data_inspection_failed"
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST
                    && e.getResponseBodyAsString().contains("data_inspection_failed")) {
                return true; // Ảnh bị từ chối, coi như nhạy cảm
            }
            // Xử lý các lỗi khác (log hoặc throw lại)
            throw e;
        }
        return false;
    }
}
