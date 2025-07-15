package com.quizletclone.flashcard.repository;

import com.quizletclone.flashcard.model.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Integer> {
} 