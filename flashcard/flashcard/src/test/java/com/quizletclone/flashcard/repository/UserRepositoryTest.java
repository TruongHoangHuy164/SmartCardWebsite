package com.quizletclone.flashcard.repository;

import com.quizletclone.flashcard.model.User;
import jakarta.persistence.EntityManager;
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
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    TestEntityManager tem;
    @Autowired
    EntityManager em;

    // Dọn DB theo thứ tự phụ thuộc để tránh FK (không sửa model)
    private void cleanup() {
        tem.flush();
        em.createNativeQuery("DELETE FROM quizzes").executeUpdate();
        em.createNativeQuery("DELETE FROM flashcards").executeUpdate();
        em.createNativeQuery("DELETE FROM decks").executeUpdate();
        em.createNativeQuery("DELETE FROM users").executeUpdate();
        em.createNativeQuery("DELETE FROM roles").executeUpdate();
        tem.clear();
    }

    @BeforeEach
    void setUp() {
        cleanup();
    }

    private User mkUser(String username, String email) {
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword("pw");
        u.setCreatedAt(new Date());
        u.setEnabled(true);
        return u;
    }

    // ======= 3 TEST PASS =======

    @Test
    void save_and_findById_shouldWork_PASS() {
        User saved = userRepository.save(mkUser("alice", "alice@example.com"));
        assertThat(userRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void update_shouldPersistChanges_PASS() {
        User saved = userRepository.save(mkUser("bob", "bob@example.com"));
        saved.setEnabled(false);
        userRepository.saveAndFlush(saved);
        tem.clear();
        var reloaded = userRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getEnabled()).isFalse();
    }

    @Test
    void findById_whenNotExists_shouldBeEmpty_PASS() {
        assertThat(userRepository.findById(-1)).isEmpty(); // id là Integer
    }

    // ======= 3 TEST FAIL (CỐ Ý) =======

    @Test
    void wrongEmailExpectation_FAIL() {
        User saved = userRepository.save(mkUser("wrong", "wrong@example.com"));
        // Cố tình sai: mong đợi email khác
        assertThat(saved.getEmail()).isEqualTo("not_the_same@example.com");
    }

    @Test
    void findAll_countTooHighExpectation_FAIL() {
        userRepository.save(mkUser("c1", "c1@example.com"));
        userRepository.save(mkUser("c2", "c2@example.com"));
        // Cố tình sai: đòi >= 3 trong khi chỉ có 2
        assertThat(userRepository.findAll().size()).isGreaterThanOrEqualTo(3);
    }

    @Test
    void delete_then_expectStillPresent_FAIL() {
        User saved = userRepository.save(mkUser("toDel", "toDel@example.com"));
        userRepository.deleteById(saved.getId());
        // Cố tình sai: mong đợi vẫn còn
        assertThat(userRepository.findById(saved.getId())).isPresent();
    }
}
