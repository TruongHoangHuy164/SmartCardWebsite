package com.quizletclone.flashcard.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.quizletclone.flashcard.model.Quiz;

public interface QuizRepository extends JpaRepository<Quiz, Integer> {
    Optional<Quiz> findTopByUserIdAndDeckIdOrderByCreatedAtDesc(Integer userId, Integer deckId);

    @Query("SELECT COUNT(qr) FROM QuizQuestion qr WHERE qr.flashcard.deck.id = :deckId")
    int countQuestionsByDeckId(@Param("deckId") Integer deckId);

    List<Quiz> findByUserId(Integer userId);
}