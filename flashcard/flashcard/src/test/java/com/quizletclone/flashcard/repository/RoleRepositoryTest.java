package com.quizletclone.flashcard.repository;

// import com.quizletclone.flashcard.model.Role;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.*;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static com.quizletclone.flashcard.testsupport.DbReset.reset;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRepositoryTest {

    @Autowired
    TestEntityManager tem;
    @Autowired
    RoleRepository roleRepository;

    @BeforeEach
    @Transactional
    void setUp() {
        reset(tem);
    }

    private com.quizletclone.flashcard.model.Role mkRole(String name) {
        com.quizletclone.flashcard.model.Role r = new com.quizletclone.flashcard.model.Role();
        r.setName(name);
        return r;
    }

    @Test
    void save_and_findById_shouldWork() {
        var saved = roleRepository.saveAndFlush(mkRole("USER"));
        assertThat(saved.getId()).isNotNull();
        var found = roleRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("USER");
    }

    @Test
    void update_shouldPersistChanges() {
        var saved = roleRepository.saveAndFlush(mkRole("ADMIN"));
        saved.setName("SUPERADMIN");
        roleRepository.saveAndFlush(saved);
        tem.clear();
        var reloaded = roleRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getName()).isEqualTo("SUPERADMIN");
    }

    @Test
    void findAll_shouldReturnSeeded() {
        roleRepository.saveAndFlush(mkRole("USER"));
        roleRepository.saveAndFlush(mkRole("ADMIN"));
        tem.flush();
        var all = roleRepository.findAll();
        assertThat(all.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void deleteById_shouldRemove() {
        var saved = roleRepository.saveAndFlush(mkRole("TODELETE"));
        Integer id = saved.getId();
        roleRepository.deleteById(id);
        assertThat(roleRepository.findById(id)).isEmpty();
    }

    @Test
    void findById_whenNotExists_shouldBeEmpty() {
        assertThat(roleRepository.findById(-1)).isEmpty();
    }
}
