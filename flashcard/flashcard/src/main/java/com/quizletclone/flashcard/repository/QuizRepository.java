package com.quizletclone.flashcard.repository;

import com.quizletclone.flashcard.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Integer> {
} 