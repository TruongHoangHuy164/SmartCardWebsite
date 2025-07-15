package com.quizletclone.flashcard.repository;

import com.quizletclone.flashcard.model.Deck;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeckRepository extends JpaRepository<Deck, Integer> {
} 