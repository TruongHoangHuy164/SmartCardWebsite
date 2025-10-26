package com.quizletclone.flashcard.service.exam;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class ExamServiceLoopTest {
    // Helper to access private method
    private String invokeGenerateRandomCode(ExamService service, int length) {
        try {
            var method = ExamService.class.getDeclaredMethod("generateRandomCode", int.class);
            method.setAccessible(true);
            return (String) method.invoke(service, length);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void testGenerateRandomCode_LengthZero() {
        ExamService service = new ExamService();
        String code = invokeGenerateRandomCode(service, 0);
        assertEquals(0, code.length());
    }
    @Test
    void testGenerateRandomCode_LengthOne() {
        ExamService service = new ExamService();
        String code = invokeGenerateRandomCode(service, 1);
        assertEquals(1, code.length());
        assertTrue(code.matches("[A-Za-z0-9]"));
    }
    @Test
    void testGenerateRandomCode_LengthSmall() {
        ExamService service = new ExamService();
        int n = 5;
        String code = invokeGenerateRandomCode(service, n);
        assertEquals(n, code.length());
        assertTrue(code.matches("[A-Za-z0-9]{" + n + "}"));
    }
    @Test
    void testGenerateRandomCode_LengthLarge() {
        ExamService service = new ExamService();
        int n = 100;
        String code = invokeGenerateRandomCode(service, n);
        assertEquals(n, code.length());
        assertTrue(code.matches("[A-Za-z0-9]{" + n + "}"));
    }
}
