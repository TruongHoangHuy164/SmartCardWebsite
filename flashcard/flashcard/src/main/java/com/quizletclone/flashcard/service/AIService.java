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
            requestBody.put("model", "google/gemma-7b-it"); // hoặc model bạn muốn requestBody.put("messages",
                                                            // messages);

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
    String aiPrompt = "Bạn là một chuyên gia giáo dục. "
        + "Hãy tìm kiếm và liệt kê " + count + " thuật ngữ quan trọng, phổ biến hoặc liên quan nhất đến chủ đề: '" + prompt + "'. "
        + "Với mỗi thuật ngữ, hãy cung cấp một định nghĩa ngắn gọn, dễ hiểu, đúng với chủ đề, bằng tiếng Việt. "
        + "Trả về duy nhất một chuỗi JSON dạng: "
        + "[{\"term\": \"...\", \"definition\": \"...\"}, ...] "
        + "Không giải thích gì thêm, chỉ trả về JSON.";

    String aiResult = ask(aiPrompt);

    try {
        ObjectMapper mapper = new ObjectMapper();
        list = Arrays.asList(mapper.readValue(aiResult, Flashcard[].class));
    } catch (Exception e) {
        e.printStackTrace();
        // Fallback: sinh flashcard mặc định nếu AI fail
        while (list.size() < count) {
            Flashcard fc = new Flashcard();
            fc.setTerm(prompt + " - Thuật ngữ " + (list.size() + 1));
            fc.setDefinition("Định nghĩa cho " + prompt + " số " + (list.size() + 1));
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
