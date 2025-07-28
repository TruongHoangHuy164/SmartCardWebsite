package com.quizletclone.flashcard.controller.exam;

import com.quizletclone.flashcard.model.exam.ExamAttemptAnswer;
import com.quizletclone.flashcard.model.exam.ExamAttempt;
import com.quizletclone.flashcard.service.exam.ExamAttemptAnswerService;
import com.quizletclone.flashcard.service.exam.ExamAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/exams/attempt-answers")
public class ExamAttemptAnswerMvcController {
    @Autowired
    private ExamAttemptAnswerService examAttemptAnswerService;
    @Autowired
    private ExamAttemptService examAttemptService;

    @GetMapping("/list/{attemptId}")
    public String listAnswers(@PathVariable Long attemptId, Model model) {
        examAttemptService.getById(attemptId)
                .ifPresent(a -> model.addAttribute("answers", examAttemptAnswerService.getAnswersByAttempt(a)));
        model.addAttribute("attemptId", attemptId);
        return "exam/attempt_answer_list";
    }
}