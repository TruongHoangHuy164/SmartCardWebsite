package com.quizletclone.flashcard.controller;

import com.quizletclone.flashcard.model.Flashcard;
import com.quizletclone.flashcard.service.FlashcardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/flashcards")
public class FlashcardController {
    @Autowired
    private FlashcardService flashcardService;

    @GetMapping
    public List<Flashcard> getAllFlashcards() {
        return flashcardService.getAllFlashcards();
    }

    @GetMapping("/{id}")
    public Optional<Flashcard> getFlashcardById(@PathVariable Integer id) {
        return flashcardService.getFlashcardById(id);
    }

    @PostMapping
    public Flashcard createFlashcard(@RequestBody Flashcard flashcard) {
        return flashcardService.saveFlashcard(flashcard);
    }

    @DeleteMapping("/{id}")
    public void deleteFlashcard(@PathVariable Integer id) {
        flashcardService.deleteFlashcard(id);
    }
} 