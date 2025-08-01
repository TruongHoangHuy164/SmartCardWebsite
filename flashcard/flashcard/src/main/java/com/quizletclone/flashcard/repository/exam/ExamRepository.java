
package com.quizletclone.flashcard.repository.exam;

import com.quizletclone.flashcard.model.exam.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    Optional<Exam> findByCode(String code);
}