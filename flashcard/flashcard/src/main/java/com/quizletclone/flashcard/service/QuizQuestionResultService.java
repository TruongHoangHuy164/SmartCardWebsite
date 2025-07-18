package com.quizletclone.flashcard.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quizletclone.flashcard.model.QuizQuestion;
import com.quizletclone.flashcard.model.QuizQuestionResult;
import com.quizletclone.flashcard.repository.QuizQuestionResultRepository;

@Service
public class QuizQuestionResultService {

    @Autowired
    private QuizQuestionResultRepository quizQuestionResultRepository;

    public QuizQuestionResult saveResult(QuizQuestionResult result) {
        return quizQuestionResultRepository.save(result);
    }

    public List<QuizQuestionResult> getResultsByQuizQuestion(QuizQuestion quizQuestion) {
        return quizQuestionResultRepository.findByQuizQuestion(quizQuestion);
    }

    public Optional<QuizQuestionResult> getById(Integer id) {
        return quizQuestionResultRepository.findById(id);
    }

    public int countCorrectByQuiz(Integer quizId) {
        return quizQuestionResultRepository.countByQuizQuestion_Quiz_IdAndIsCorrectTrue(quizId);
    }
}
