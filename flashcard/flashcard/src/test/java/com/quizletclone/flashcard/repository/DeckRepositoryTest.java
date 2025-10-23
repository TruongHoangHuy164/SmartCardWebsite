package com.quizletclone.flashcard.repository;

import com.quizletclone.flashcard.model.Deck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DeckRepositoryTest {

    @Autowired
    DeckRepository deckRepository;

    // THÊM: các repo con để xóa theo thứ tự phụ thuộc
    @Autowired
    FlashcardRepository flashcardRepository;
    @Autowired
    QuizQuestionRepository quizQuestionRepository; // nếu có
    @Autowired
    QuizQuestionResultRepository quizQuestionResultRepository; // nếu có
    @Autowired
    QuizRepository quizRepository; // nếu có

    @Autowired
    TestEntityManager tem;

    Integer publicDeckId;

    private void cleanup() {
        // XÓA THEO THỨ TỰ: results -> questions -> flashcards -> quizzes -> decks
        if (quizQuestionResultRepository != null)
            quizQuestionResultRepository.deleteAllInBatch();
        if (quizQuestionRepository != null)
            quizQuestionRepository.deleteAllInBatch();
        flashcardRepository.deleteAllInBatch();
        if (quizRepository != null)
            quizRepository.deleteAllInBatch();
        deckRepository.deleteAllInBatch();
    }

    @BeforeEach
    void setUp() {
        cleanup();

        Deck d1 = new Deck();
        d1.setTitle("Java Basics");
        d1.setDescription("Intro to Java");
        d1.setSubject("Java");
        d1.setIsPublic(true);
        d1.setCreatedAt(new Date());
        tem.persist(d1);

        Deck d2 = new Deck();
        d2.setTitle("SQL Advanced");
        d2.setDescription("Window functions");
        d2.setSubject("SQL");
        d2.setIsPublic(false);
        d2.setCreatedAt(new Date());
        tem.persist(d2);

        tem.flush();
        publicDeckId = d1.getId();
        tem.clear();
    }

    @Test
    void save_and_findById_shouldWork() {
        Deck d = new Deck();
        d.setTitle("Spring Boot");
        d.setDescription("Starters & AutoConfig");
        d.setSubject("Spring");
        d.setIsPublic(true);
        d.setCreatedAt(new Date());

        Deck saved = deckRepository.save(d);
        assertThat(saved.getId()).isNotNull();

        var found = deckRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Spring Boot");
    }

    @Test
    void findAllByIsPublicTrue_shouldReturnOnlyPublicDecks() {
        var result = deckRepository.findAllByIsPublicTrue();
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(Deck::getIsPublic);
    }

    @Test
    void findAllByIsPublicFalse_shouldReturnOnlyPrivateDecks() {
        var result = deckRepository.findAllByIsPublicFalse();
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(deck -> !deck.getIsPublic());
    }

        @Test
    void update_shouldPersistChanges() {
        var deck = deckRepository.findById(publicDeckId).orElseThrow();
        deck.setTitle("Java Basics - Updated");
        deckRepository.saveAndFlush(deck);

        tem.clear();
        var reloaded = deckRepository.findById(publicDeckId).orElseThrow();
        assertThat(reloaded.getTitle()).isEqualTo("Java Basics - Updated");
    }

        @Test
    void findAllBySubject_shouldReturnOnlyMatchingSubject() {
        var result = deckRepository.findAllBySubject("Java");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Java Basics");
    }
}
