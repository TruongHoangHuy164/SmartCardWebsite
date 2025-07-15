package com.quizletclone.flashcard.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quizletclone.flashcard.model.Flashcard;
import com.quizletclone.flashcard.repository.FlashcardRepository;

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

    public long getFlashcardCountByDeckId(Integer deckId) {
        return flashcardRepository.countByDeckId(deckId);
    }

    public Flashcard saveFlashcard(Flashcard flashcard) {
        return flashcardRepository.save(flashcard);
    }

    public void deleteFlashcard(Integer id) {
        flashcardRepository.deleteById(id);
    }
}