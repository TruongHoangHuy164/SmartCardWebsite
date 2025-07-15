package com.quizletclone.flashcard.repository;

import com.quizletclone.flashcard.model.SpacedRepetition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpacedRepetitionRepository extends JpaRepository<SpacedRepetition, Integer> {
} 