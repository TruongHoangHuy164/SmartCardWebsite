package com.quizletclone.flashcard.repository.exam;

import com.quizletclone.flashcard.model.exam.ExamOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamOptionRepository extends JpaRepository<ExamOption, Long> {
}