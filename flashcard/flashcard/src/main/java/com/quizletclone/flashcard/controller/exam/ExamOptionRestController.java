package com.quizletclone.flashcard.controller.exam;

import com.quizletclone.flashcard.model.exam.ExamOption;
import com.quizletclone.flashcard.model.exam.ExamQuestion;
import com.quizletclone.flashcard.service.exam.ExamOptionService;
import com.quizletclone.flashcard.service.exam.ExamQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/exam-options")
public class ExamOptionRestController {
    @Autowired
    private ExamOptionService examOptionService;
    @Autowired
    private ExamQuestionService examQuestionService;

    @PostMapping
    public ResponseEntity<ExamOption> addOption(@RequestBody ExamOption option) {
        return ResponseEntity.ok(examOptionService.addOption(option));
    }

    @GetMapping("/by-question/{questionId}")
    public ResponseEntity<List<ExamOption>> getOptionsByQuestion(@PathVariable Long questionId) {
        return examQuestionService.getById(questionId)
                .map(q -> ResponseEntity.ok(examOptionService.getOptionsByQuestion(q)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}