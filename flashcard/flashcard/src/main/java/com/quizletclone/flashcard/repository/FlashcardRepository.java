package com.quizletclone.flashcard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quizletclone.flashcard.model.Flashcard;

public interface FlashcardRepository extends JpaRepository<Flashcard, Integer> {
    List<Flashcard> findByDeckId(Integer deckId);

    long countByDeckId(Integer deckId);
}