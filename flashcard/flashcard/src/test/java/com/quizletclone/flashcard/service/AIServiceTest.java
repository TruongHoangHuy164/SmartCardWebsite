package com.quizletclone.flashcard.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class AIServiceTest {
    private AIService spyService;

    @BeforeEach
    void setUp() {
        spyService = Mockito.spy(new AIService());
        ReflectionTestUtils.setField(spyService, "apiKey", "test-key");
    }

    @Test
    void testIsSensitiveText_yes() {
        Mockito.doReturn("YES").when(spyService).ask(Mockito.anyString());
        assertTrue(spyService.isSensitiveText("chửi bậy, tục tĩu"));
    }

    @Test
    void testIsSensitiveText_no() {
        Mockito.doReturn("NO").when(spyService).ask(Mockito.anyString());
        assertFalse(spyService.isSensitiveText("Nội dung bình thường"));
    }

    @Test
    void testIsSensitiveImage_yes() {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[] { 1, 2, 3 });
        AIService mockService = Mockito.mock(AIService.class);
        Mockito.when(mockService.isSensitiveImage(file)).thenReturn(true);
        assertTrue(mockService.isSensitiveImage(file));
    }

    @Test
    void testIsSensitiveImage_no() {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[] { 1, 2, 3 });
        AIService mockService = Mockito.mock(AIService.class);
        Mockito.when(mockService.isSensitiveImage(file)).thenReturn(false);
        assertFalse(mockService.isSensitiveImage(file));
    }

    @Test
    void testIsSensitiveImage_emptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", new byte[] {});
        assertFalse(spyService.isSensitiveImage(file));
    }
}
