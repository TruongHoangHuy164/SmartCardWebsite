package com.quizletclone.flashcard.controller.exam;

import com.quizletclone.flashcard.model.exam.Exam;
import com.quizletclone.flashcard.model.exam.ExamQuestion;
import com.quizletclone.flashcard.service.exam.ExamQuestionService;
import com.quizletclone.flashcard.service.exam.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/exam-questions")
public class ExamQuestionRestController {
    @Autowired
    private ExamQuestionService examQuestionService;
    @Autowired
    private ExamService examService;

    @PostMapping
    public ResponseEntity<ExamQuestion> addQuestion(@RequestBody ExamQuestion question) {
        return ResponseEntity.ok(examQuestionService.addQuestion(question));
    }

    @GetMapping("/by-exam/{examId}")
    public ResponseEntity<List<ExamQuestion>> getQuestionsByExam(@PathVariable Long examId) {
        Optional<Exam> exam = examService.getExamById(examId);
        return exam.map(e -> ResponseEntity.ok(examQuestionService.getQuestionsByExam(e)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}