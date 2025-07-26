package com.quizletclone.flashcard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quizletclone.flashcard.model.QuizQuestion;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Integer> {
    List<QuizQuestion> findByFlashcard_Deck_Id(Integer deckId);

    int countByFlashcard_Deck_Id(Integer deckId);

    List<QuizQuestion> findByFlashcard_Id(Integer flashcardId);
}