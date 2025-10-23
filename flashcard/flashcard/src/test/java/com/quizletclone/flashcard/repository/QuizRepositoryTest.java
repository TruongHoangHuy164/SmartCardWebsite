package com.quizletclone.flashcard.repository;

import com.quizletclone.flashcard.model.Deck;
import com.quizletclone.flashcard.model.Quiz;
import com.quizletclone.flashcard.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static com.quizletclone.flashcard.testsupport.DbReset.reset;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class QuizRepositoryTest {

    @Autowired
    TestEntityManager tem;
    @Autowired
    QuizRepository quizRepository;
    @Autowired
    DeckRepository deckRepository;
    @Autowired
    UserRepository userRepository;

    Integer deckId;
    Integer userId;

    @BeforeEach
    @Transactional
    void setUp() {
        reset(tem);

        User u = new User();
        u.setUsername("Tester");
        u.setEmail("tester@example.com");
        u.setEnabled(true);
        u.setCreatedAt(new Date());
        tem.persist(u);

        Deck d = new Deck();
        d.setTitle("Deck for Quiz");
        d.setDescription("desc");
        d.setSubject("SUBJ");
        d.setIsPublic(true);
        d.setCreatedAt(new Date());
        d.setUser(u);
        tem.persist(d);

        tem.flush();
        userId = u.getId();
        deckId = d.getId();
        tem.clear();
    }

    private Quiz mkQuiz(User u, Deck d, int correct, int total, float score) {
        Quiz q = new Quiz();
        q.setUser(u);
        q.setDeck(d);
        q.setCorrectAnswers(correct);
        q.setTotalQuestions(total);
        q.setScore(score);
        q.setCreatedAt(new Date());
        return q;
    }

    @Test
    void save_and_findById_shouldWork() {
        var user = userRepository.findById(userId).orElseThrow();
        var deck = deckRepository.findById(deckId).orElseThrow();

        Quiz saved = quizRepository.save(mkQuiz(user, deck, 3, 5, 60f));
        assertThat(saved.getId()).isNotNull();

        var found = quizRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getCorrectAnswers()).isEqualTo(3);
        assertThat(found.get().getTotalQuestions()).isEqualTo(5);
    }

    @Test
    void update_shouldPersistChanges() {
        var user = userRepository.findById(userId).orElseThrow();
        var deck = deckRepository.findById(deckId).orElseThrow();

        Quiz saved = quizRepository.save(mkQuiz(user, deck, 1, 2, 50f));
        saved.setScore(80f);
        saved.setCorrectAnswers(2);
        quizRepository.saveAndFlush(saved);

        tem.clear();
        var reloaded = quizRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getScore()).isEqualTo(80f);
        assertThat(reloaded.getCorrectAnswers()).isEqualTo(2);
    }

    @Test
    void findAll_shouldReturnMultipleQuizzes() {
        var user = userRepository.findById(userId).orElseThrow();
        var deck = deckRepository.findById(deckId).orElseThrow();

        quizRepository.save(mkQuiz(user, deck, 0, 0, 0f));
        quizRepository.save(mkQuiz(user, deck, 5, 5, 100f));

        var all = quizRepository.findAll();
        assertThat(all.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void deleteById_shouldRemoveQuiz() {
        var user = userRepository.findById(userId).orElseThrow();
        var deck = deckRepository.findById(deckId).orElseThrow();

        Quiz saved = quizRepository.save(mkQuiz(user, deck, 1, 1, 100f));
        Integer id = saved.getId();

        quizRepository.deleteById(id);
        assertThat(quizRepository.findById(id)).isEmpty();
    }

    @Test
    void findById_whenNotExists_shouldBeEmpty() {
        assertThat(quizRepository.findById(-999)).isEmpty();
    }
}
