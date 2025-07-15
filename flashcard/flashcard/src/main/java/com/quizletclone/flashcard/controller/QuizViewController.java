package com.quizletclone.flashcard.controller;

import com.quizletclone.flashcard.model.Quiz;
import com.quizletclone.flashcard.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
@RequestMapping("/quizzes")
public class QuizViewController {
    @Autowired
    private QuizService quizService;

    @GetMapping
    public String listQuizzes(Model model) {
        List<Quiz> quizzes = quizService.getAllQuizzes();
        model.addAttribute("quizzes", quizzes);
        return "quiz/list";
    }

    @GetMapping("/detail/{id}")
    public String quizDetail(@PathVariable Integer id, Model model) {
        quizService.getQuizById(id).ifPresent(quiz -> model.addAttribute("quiz", quiz));
        return "quiz/detail";
    }

    @GetMapping("/form")
    public String quizForm(@RequestParam(value = "id", required = false) Integer id, Model model) {
        if (id != null) {
            quizService.getQuizById(id).ifPresent(quiz -> model.addAttribute("quiz", quiz));
        } else {
            model.addAttribute("quiz", new com.quizletclone.flashcard.model.Quiz());
        }
        return "quiz/form";
    }

    @PostMapping("/save")
    public String saveQuiz(@ModelAttribute("quiz") com.quizletclone.flashcard.model.Quiz quiz) {
        quizService.saveQuiz(quiz);
        return "redirect:/quizzes";
    }
} 