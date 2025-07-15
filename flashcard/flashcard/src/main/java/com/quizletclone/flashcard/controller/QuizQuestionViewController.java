package com.quizletclone.flashcard.controller;

import com.quizletclone.flashcard.model.QuizQuestion;
import com.quizletclone.flashcard.service.QuizQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/quiz-questions")
public class QuizQuestionViewController {
    @Autowired
    private QuizQuestionService quizQuestionService;

    @GetMapping
    public String listQuizQuestions(Model model) {
        List<QuizQuestion> quizQuestions = quizQuestionService.getAllQuizQuestions();
        model.addAttribute("quizQuestions", quizQuestions);
        return "quizquestion/list";
    }

    @GetMapping("/detail/{id}")
    public String quizQuestionDetail(@PathVariable Integer id, Model model) {
        quizQuestionService.getQuizQuestionById(id).ifPresent(qq -> model.addAttribute("quizQuestion", qq));
        return "quizquestion/detail";
    }

    @GetMapping("/form")
    public String quizQuestionForm(@RequestParam(value = "id", required = false) Integer id, Model model) {
        if (id != null) {
            quizQuestionService.getQuizQuestionById(id).ifPresent(qq -> model.addAttribute("quizQuestion", qq));
        } else {
            model.addAttribute("quizQuestion", new com.quizletclone.flashcard.model.QuizQuestion());
        }
        return "quizquestion/form";
    }

    @PostMapping("/save")
    public String saveQuizQuestion(@ModelAttribute("quizQuestion") com.quizletclone.flashcard.model.QuizQuestion quizQuestion) {
        quizQuestionService.saveQuizQuestion(quizQuestion);
        return "redirect:/quiz-questions";
    }
} 