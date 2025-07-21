package com.quizletclone.flashcard.repository.exam;

import com.quizletclone.flashcard.model.exam.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
}