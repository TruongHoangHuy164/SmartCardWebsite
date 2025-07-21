package com.quizletclone.flashcard.controller.exam;

import com.quizletclone.flashcard.model.exam.Exam;
import com.quizletclone.flashcard.model.exam.ExamAttempt;
import com.quizletclone.flashcard.service.exam.ExamAttemptService;
import com.quizletclone.flashcard.service.exam.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/exam-attempts")
public class ExamAttemptRestController {
    @Autowired
    private ExamAttemptService examAttemptService;
    @Autowired
    private ExamService examService;

    @PostMapping("/create")
    public ResponseEntity<ExamAttempt> createAttempt(@RequestParam Long examId, @RequestParam String userId) {
        Optional<Exam> examOpt = examService.getExamById(examId);
        if (examOpt.isEmpty())
            return ResponseEntity.notFound().build();
        Optional<ExamAttempt> attemptOpt = examAttemptService.createAttempt(examOpt.get(), userId);
        return attemptOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("/submit")
    public ResponseEntity<ExamAttempt> submitExam(@RequestParam Long attemptId, @RequestBody Map<Long, Long> answers) {
        Optional<ExamAttempt> attemptOpt = examAttemptService.getById(attemptId);
        if (attemptOpt.isEmpty())
            return ResponseEntity.notFound().build();
        Optional<ExamAttempt> submitted = examAttemptService.submitAttempt(attemptOpt.get(), answers);
        return submitted.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/by-exam/{examId}")
    public ResponseEntity<List<ExamAttempt>> getAttemptsByExam(@PathVariable Long examId) {
        Optional<Exam> examOpt = examService.getExamById(examId);
        if (examOpt.isEmpty())
            return ResponseEntity.notFound().build();
        // Bạn cần cài đặt method getAttemptsByExam(Exam) trong ExamAttemptService nếu
        // chưa có
        List<ExamAttempt> attempts = examAttemptService.getAttemptsByExam(examOpt.get());
        return ResponseEntity.ok(attempts);
    }
}