package com.quizletclone.flashcard.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.quizletclone.flashcard.model.Flashcard;

public interface FlashcardRepository extends JpaRepository<Flashcard, Integer> {
    List<Flashcard> findByDeckId(Integer deckId);

    long countByDeckId(Integer deckId);

    @org.springframework.data.jpa.repository.Query(value = "SELECT COUNT(*) FROM flashcards WHERE MONTH(created_at) = :month AND YEAR(created_at) = :year", nativeQuery = true)
    int countFlashcardsByMonthAndYear(@org.springframework.data.repository.query.Param("month") int month,
            @org.springframework.data.repository.query.Param("year") int year);

    @Query("SELECT f FROM Flashcard f WHERE f.deck.id = :deckId")
    List<Flashcard> findAllByDeckId(@Param("deckId") Integer deckId);

    @Query("""
            SELECT f FROM Flashcard f
            WHERE LOWER(f.term) LIKE LOWER(CONCAT('%', :kw, '%'))
               OR LOWER(f.definition) LIKE LOWER(CONCAT('%', :kw, '%'))
            """)
    Page<Flashcard> searchByKeyword(@Param("kw") String keyword, Pageable pageable);
}