package com.quizletclone.flashcard.testsupport;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import jakarta.persistence.EntityManager;

public class DbReset {
    /**
     * Very small helper to clear tables used in tests. Uses native SQL - adjust if
     * your DB differs.
     */
    public static void reset(TestEntityManager tem) {
        EntityManager em = tem.getEntityManager();
        try {
            em.joinTransaction();
        } catch (Exception ignore) {
        }
        // Order matters due to FKs: results -> questions -> flashcards -> quizzes ->
        // decks -> users -> roles
        em.createNativeQuery("DELETE FROM quiz_question_results").executeUpdate();
        em.createNativeQuery("DELETE FROM quiz_questions").executeUpdate();
        em.createNativeQuery("DELETE FROM flashcards").executeUpdate();
        em.createNativeQuery("DELETE FROM quizzes").executeUpdate();
        em.createNativeQuery("DELETE FROM spaced_repetition").executeUpdate();
        em.createNativeQuery("DELETE FROM decks").executeUpdate();
        em.createNativeQuery("DELETE FROM users").executeUpdate();
        em.createNativeQuery("DELETE FROM roles").executeUpdate();
        em.flush();
    }
}
