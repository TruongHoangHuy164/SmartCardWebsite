package com.quizletclone.flashcard.repository.exam;

import com.quizletclone.flashcard.model.exam.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long> {
    Optional<ExamQuestion> findByExamId(Long id);
}