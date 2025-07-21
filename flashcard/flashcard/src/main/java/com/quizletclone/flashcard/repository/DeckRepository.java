package com.quizletclone.flashcard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quizletclone.flashcard.model.Deck;
import com.quizletclone.flashcard.model.User;

public interface DeckRepository extends JpaRepository<Deck, Integer> {
    List<Deck> findByIsPublicTrue();

    List<Deck> findByUser(User user);

    List<Deck> findByUserNot(User user);

}