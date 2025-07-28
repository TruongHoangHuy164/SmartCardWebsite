package com.quizletclone.flashcard.repository.exam;

import com.quizletclone.flashcard.model.exam.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
    void deleteByExam_Id(Long examId);
    List<ExamAttempt> findByExam_IdOrderBySubmittedAtDesc(Long examId);
}