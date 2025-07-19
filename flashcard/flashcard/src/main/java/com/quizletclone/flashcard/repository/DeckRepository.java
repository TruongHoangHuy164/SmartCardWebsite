package com.quizletclone.flashcard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quizletclone.flashcard.model.Deck;

public interface DeckRepository extends JpaRepository<Deck, Integer> {
    List<Deck> findByIsPublicTrue();
}