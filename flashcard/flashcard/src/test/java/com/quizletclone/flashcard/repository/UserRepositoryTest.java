package com.quizletclone.flashcard.repository;

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

    private void cleanup() {
        userRepository.deleteAllInBatch();
        roleRepository.deleteAllInBatch();
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

    @Test
    void save_and_findById_shouldWork() {
        User saved = userRepository.save(mkUser("alice", "alice@example.com"));
        assertThat(userRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void update_shouldPersistChanges() {
        User saved = userRepository.save(mkUser("bob", "bob@example.com"));
        saved.setEnabled(false);
        userRepository.saveAndFlush(saved);
        tem.clear();
        var reloaded = userRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getEnabled()).isFalse();
    }

    @Test
    void findAll_shouldReturn() {
        userRepository.save(mkUser("c1", "c1@example.com"));
        userRepository.save(mkUser("c2", "c2@example.com"));
        assertThat(userRepository.findAll().size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void deleteById_shouldRemove() {
        User saved = userRepository.save(mkUser("del", "del@example.com"));
        userRepository.deleteById(saved.getId());
        assertThat(userRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void findById_whenNotExists_shouldBeEmpty() {
        assertThat(userRepository.findById(-1)).isEmpty();
    }
}
