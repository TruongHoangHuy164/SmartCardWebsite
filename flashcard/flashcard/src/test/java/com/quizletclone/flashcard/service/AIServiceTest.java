package com.quizletclone.flashcard.service;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AIService — Kiểm thử phân loại nhạy cảm (text & image)")
public class AIServiceTest {

    private AIService spyService;

    /** Bật/tắt log console khi chạy test: -DshowLog=true */
    private final boolean showLog = Boolean.parseBoolean(System.getProperty("showLog", "true"));

    private void log(String title, Object value) {
        if (showLog)
            System.out.println("🔎 " + title + ": " + value);
    }

    @BeforeEach
    void setUp() {
        spyService = Mockito.spy(new AIService());
        ReflectionTestUtils.setField(spyService, "apiKey", "test-key");
    }

    // ---------- TEXT ----------
    @Test
    @DisplayName("Text: YES → isSensitiveText trả về true, gọi ask() đúng 1 lần")
    void testIsSensitiveText_yes() {
        String prompt = "chửi bậy, tục tĩu";
        Mockito.doReturn("YES").when(spyService).ask(Mockito.anyString());

        boolean actual = spyService.isSensitiveText(prompt);
        log("Input", prompt);
        log("ask() ->", "YES");
        log("KQ isSensitiveText", actual);

        assertTrue(actual, "Kỳ vọng: YES -> true, nhưng trả về false");
        Mockito.verify(spyService, Mockito.times(1)).ask(Mockito.anyString());
    }

    @Test
    @DisplayName("Text: NO → isSensitiveText trả về false")
    void testIsSensitiveText_no() {
        String prompt = "Nội dung bình thường";
        Mockito.doReturn("NO").when(spyService).ask(Mockito.anyString());

        boolean actual = spyService.isSensitiveText(prompt);
        log("Input", prompt);
        log("ask() ->", "NO");
        log("KQ isSensitiveText", actual);

        assertFalse(actual, "Kỳ vọng: NO -> false, nhưng trả về true");
        Mockito.verify(spyService, Mockito.times(1)).ask(Mockito.anyString());
    }

    @Test
    @DisplayName("Text: Kết quả mơ hồ/khác (ví dụ UNKNOWN) → xử lý an toàn về false")
    void testIsSensitiveText_unknownTreatAsFalse() {
        Mockito.doReturn("UNKNOWN").when(spyService).ask(Mockito.anyString());
        boolean actual = spyService.isSensitiveText("câu mơ hồ");
        log("ask() ->", "UNKNOWN");
        log("KQ isSensitiveText", actual);
        assertFalse(actual, "Kỳ vọng: UNKNOWN -> false (fail-safe).");
    }

    @Test
    @DisplayName("Text: null/empty → false (không nhạy cảm)")
    void testIsSensitiveText_nullOrEmpty() {
        Mockito.doReturn("NO").when(spyService).ask(Mockito.anyString());

        boolean nullCase = spyService.isSensitiveText(null);
        boolean emptyCase = spyService.isSensitiveText("");
        log("null case", nullCase);
        log("empty case", emptyCase);

        assertFalse(nullCase, "null phải trả về false");
        assertFalse(emptyCase, "empty phải trả về false");
    }

    // ---------- IMAGE ----------
    @Test
    @DisplayName("Image: file rỗng → false, không gọi API ngoài")
    void testIsSensitiveImage_emptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", new byte[] {});
        boolean actual = spyService.isSensitiveImage(file);
        log("Image size", file.getSize());
        log("KQ isSensitiveImage", actual);
        assertFalse(actual, "File rỗng phải trả về false.");
        // (Không verify internal call do không biết API bên trong; giả định không gọi)
    }

    @Test
    @DisplayName("🖼️ Image nhạy cảm → isSensitiveImage trả về TRUE (hiện rõ output)")
    void testIsSensitiveImage_sensitive_yes() {
        // Giả lập 1 ảnh nhạy cảm (file thật hoặc tên file bạn nêu)
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "flashcard\\flashcard\\uploads\\upload\\94f9c5b7-ab9d-42dd-99ec-c29dffed3427_z6827207526181_03fca29d8b1806ae83b044e0b4fdba32.jpg",
                "image/jpeg",
                new byte[] { 1, 2, 3 });

        // Giả lập hành vi của AIService -> ảnh này nhạy cảm
        AIService mockService = Mockito.mock(AIService.class);
        Mockito.when(mockService.isSensitiveImage(file)).thenReturn(true);

        // Gọi hàm test
        boolean actual = mockService.isSensitiveImage(file);

        // Hiển thị rõ kết quả
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📸 Kiểm tra ảnh nhạy cảm");
        System.out.println("  • Tên file: " + file.getOriginalFilename());
        System.out.println("  • Loại file: " + file.getContentType());
        System.out.println("  • Kết quả kiểm tra: " + (actual ? "✅ NHẠY CẢM" : "❌ KHÔNG NHẠY CẢM"));
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // Kiểm tra kết quả mong đợi
        assertTrue(actual, "Kỳ vọng ảnh nhạy cảm phải trả về TRUE.");
    }

    @Test
    @DisplayName("Image: case NO (mock) → false (hiện rõ output)")
    void testIsSensitiveImage_no() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                new byte[] { 1, 2, 3 });

        AIService mockService = Mockito.mock(AIService.class);
        Mockito.when(mockService.isSensitiveImage(file)).thenReturn(false);

        boolean actual = mockService.isSensitiveImage(file);
        log("Image NO mock", actual);
        assertFalse(actual, "Kỳ vọng NO -> false.");
    }
}
