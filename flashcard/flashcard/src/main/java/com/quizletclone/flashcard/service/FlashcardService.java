package com.quizletclone.flashcard.service;

import com.quizletclone.flashcard.model.Flashcard;
import com.quizletclone.flashcard.repository.FlashcardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FlashcardService {
    @Autowired
    private FlashcardRepository flashcardRepository;

    public List<Flashcard> getAllFlashcards() {
        return flashcardRepository.findAll();
    }

    public Optional<Flashcard> getFlashcardById(Integer id) {
        return flashcardRepository.findById(id);
    }

    public Flashcard saveFlashcard(Flashcard flashcard) {
        return flashcardRepository.save(flashcard);
    }

    public void deleteFlashcard(Integer id) {
        flashcardRepository.deleteById(id);
    }
} 