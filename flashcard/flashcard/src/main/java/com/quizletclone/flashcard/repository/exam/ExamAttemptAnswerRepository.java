package com.quizletclone.flashcard.repository.exam;

import com.quizletclone.flashcard.model.exam.ExamAttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamAttemptAnswerRepository extends JpaRepository<ExamAttemptAnswer, Long> {
}