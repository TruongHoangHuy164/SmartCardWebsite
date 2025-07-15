package com.quizletclone.flashcard.repository;

import com.quizletclone.flashcard.model.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlashcardRepository extends JpaRepository<Flashcard, Integer> {
} 