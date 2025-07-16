package com.quizletclone.flashcard.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.quizletclone.flashcard.model.Flashcard;
import com.quizletclone.flashcard.service.FlashcardService;

@Controller
@RequestMapping("/study")
public class StudyController {

    @Autowired
    private FlashcardService flashcardService;

    @GetMapping("/deck/{deckId}")
    public String studyDeck(@PathVariable Integer deckId, Model model) {
        List<Flashcard> flashcards = flashcardService.getFlashcardsByDeckId(deckId);

        if (flashcards == null || flashcards.isEmpty()) {
            flashcards = new ArrayList<>(); // tránh null
        }

        model.addAttribute("flashcards", flashcards);
        return "study/deck"; // templates/study/deck.html
    }
}
