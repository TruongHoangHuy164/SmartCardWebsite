package com.quizletclone.flashcard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quizletclone.flashcard.model.QuizQuestion;
import com.quizletclone.flashcard.model.QuizQuestionResult;

public interface QuizQuestionResultRepository extends JpaRepository<QuizQuestionResult, Integer> {
    List<QuizQuestionResult> findByQuizQuestion(QuizQuestion quizQuestion);

    int countByQuizQuestion_Quiz_IdAndIsCorrectTrue(Integer quizId);

}
