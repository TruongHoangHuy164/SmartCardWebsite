package com.quizletclone.flashcard.repository;

import com.quizletclone.flashcard.model.Deck;
import com.quizletclone.flashcard.model.Flashcard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FlashcardRepositoryTest {

    @Autowired
    FlashcardRepository flashcardRepository;
    @Autowired
    DeckRepository deckRepository;

    // THÊM: repo con để xóa trước khi xóa flashcards
    @Autowired
    QuizQuestionRepository quizQuestionRepository; // nếu có
    @Autowired
    QuizQuestionResultRepository quizQuestionResultRepository; // nếu có

    @Autowired
    TestEntityManager tem;

    Integer deckId;

    private void cleanup() {
        if (quizQuestionResultRepository != null)
            quizQuestionResultRepository.deleteAllInBatch();
        if (quizQuestionRepository != null)
            quizQuestionRepository.deleteAllInBatch();
        flashcardRepository.deleteAllInBatch();
        deckRepository.deleteAllInBatch();
    }

    @BeforeEach
    void seed() {
        cleanup();

        Deck deck = new Deck();
        deck.setTitle("Java Basics");
        deck.setDescription("Intro");
        deck.setSubject("Java");
        deck.setIsPublic(true);
        deck.setCreatedAt(new Date());
        tem.persist(deck);

        Flashcard c1 = new Flashcard();
        c1.setDeck(deck);
        c1.setTerm("JVM");
        c1.setDefinition("Java Virtual Machine");
        c1.setCreatedAt(new Date());
        tem.persist(c1);

        Flashcard c2 = new Flashcard();
        c2.setDeck(deck);
        c2.setTerm("JDK");
        c2.setDefinition("Java Development Kit");
        c2.setCreatedAt(new Date());
        tem.persist(c2);

        tem.flush();
        deckId = deck.getId();
        tem.clear();
    }

    @Test
    void findAllByDeckId_shouldReturnCardsOfDeck() {
        var list = flashcardRepository.findAllByDeckId(deckId);
        assertThat(list).hasSize(2);
        assertThat(list).extracting(Flashcard::getTerm)
                .containsExactlyInAnyOrder("JVM", "JDK");
    }

    @Test
    void searchByKeyword_shouldMatchTermOrDefinition_caseInsensitive() {
        var page = flashcardRepository.searchByKeyword("java", PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    void save_shouldAssignId_andLinkToDeck() {
        var deck = deckRepository.findById(deckId).orElseThrow();

        Flashcard c = new Flashcard();
        c.setDeck(deck);
        c.setTerm("JRE");
        c.setDefinition("Java Runtime Environment");
        c.setCreatedAt(new Date());

        var saved = flashcardRepository.save(c);
        assertThat(saved.getId()).isNotNull();

        var cards = flashcardRepository.findAllByDeckId(deckId);
        assertThat(cards).extracting(Flashcard::getTerm).contains("JRE");
    }

    @Test
    void searchByKeyword_shouldBeCaseInsensitive_andMatchDefinition() {
        // "VIRTUAL" khớp phần definition "Java Virtual Machine" của thẻ JVM
        var page = flashcardRepository.searchByKeyword("VIRTUAL", PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getTerm()).isEqualTo("JVM");
    }

    @Test
    void searchByKeyword_shouldReturnEmpty_whenNoMatch() {
        var page = flashcardRepository.searchByKeyword("no-such-keyword", PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isZero();
        assertThat(page.getContent()).isEmpty();
    }
}
