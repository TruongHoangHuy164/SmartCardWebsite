package com.quizletclone.flashcard.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quizletclone.flashcard.model.Quiz;
import com.quizletclone.flashcard.repository.QuizRepository;

@Service
public class QuizService {
    @Autowired
    private QuizRepository quizRepository;

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public Optional<Quiz> getQuizById(Integer id) {
        return quizRepository.findById(id);
    }

    public Quiz saveQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public void deleteQuiz(Integer id) {
        quizRepository.deleteById(id);
    }

    public int getCorrectAnswersFromLatestQuiz(int userId, int deckId) {
        Optional<Quiz> quizOpt = quizRepository.findTopByUserIdAndDeckIdOrderByCreatedAtDesc(userId, deckId);
        if (quizOpt.isPresent()) {
            Quiz quiz = quizOpt.get();
            Integer correctAnswers = quiz.getCorrectAnswers();
            return (correctAnswers != null) ? correctAnswers : 0;
        }
        return 0;
    }
}