package com.quizletclone.flashcard.controller;

import com.quizletclone.flashcard.model.QuizQuestion;
import com.quizletclone.flashcard.service.QuizQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quiz-questions")
public class QuizQuestionController {
    @Autowired
    private QuizQuestionService quizQuestionService;

    @GetMapping
    public List<QuizQuestion> getAllQuizQuestions() {
        return quizQuestionService.getAllQuizQuestions();
    }

    @GetMapping("/{id}")
    public Optional<QuizQuestion> getQuizQuestionById(@PathVariable Integer id) {
        return quizQuestionService.getQuizQuestionById(id);
    }

    @PostMapping
    public QuizQuestion createQuizQuestion(@RequestBody QuizQuestion quizQuestion) {
        return quizQuestionService.saveQuizQuestion(quizQuestion);
    }

    @DeleteMapping("/{id}")
    public void deleteQuizQuestion(@PathVariable Integer id) {
        quizQuestionService.deleteQuizQuestion(id);
    }
} 