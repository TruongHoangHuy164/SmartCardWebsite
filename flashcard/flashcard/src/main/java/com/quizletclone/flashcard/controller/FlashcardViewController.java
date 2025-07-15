package com.quizletclone.flashcard.controller;

import com.quizletclone.flashcard.model.Flashcard;
import com.quizletclone.flashcard.service.FlashcardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/flashcards")
public class FlashcardViewController {
    @Autowired
    private FlashcardService flashcardService;

    @GetMapping
    public String listFlashcards(Model model) {
        List<Flashcard> flashcards = flashcardService.getAllFlashcards();
        model.addAttribute("flashcards", flashcards);
        return "flashcard/list";
    }

    @GetMapping("/detail/{id}")
    public String flashcardDetail(@PathVariable Integer id, Model model) {
        flashcardService.getFlashcardById(id).ifPresent(flashcard -> model.addAttribute("flashcard", flashcard));
        return "flashcard/detail";
    }

    @GetMapping("/form")
    public String flashcardForm(@RequestParam(value = "id", required = false) Integer id, Model model) {
        if (id != null) {
            flashcardService.getFlashcardById(id).ifPresent(flashcard -> model.addAttribute("flashcard", flashcard));
        } else {
            model.addAttribute("flashcard", new com.quizletclone.flashcard.model.Flashcard());
        }
        return "flashcard/form";
    }

    @PostMapping("/save")
    public String saveFlashcard(@ModelAttribute("flashcard") com.quizletclone.flashcard.model.Flashcard flashcard, RedirectAttributes redirectAttributes) {
        flashcardService.saveFlashcard(flashcard);
        redirectAttributes.addFlashAttribute("toast", "Lưu flashcard thành công!");
        return "redirect:/flashcards";
    }
} 