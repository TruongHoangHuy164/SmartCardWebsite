package com.quizletclone.flashcard.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.quizletclone.flashcard.model.Deck;

public interface DeckRepository extends JpaRepository<Deck, Integer> {
    List<Deck> findByIsPublicTrue();
    Page<Deck> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Deck> findBySubjectIgnoreCase(String subject, Pageable pageable);
    Page<Deck> findByTitleContainingIgnoreCaseAndSubjectIgnoreCase(String keyword, String subject, Pageable pageable);
}