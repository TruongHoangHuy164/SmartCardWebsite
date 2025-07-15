package com.quizletclone.flashcard.service;

import com.quizletclone.flashcard.model.QuizQuestion;
import com.quizletclone.flashcard.repository.QuizQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuizQuestionService {
    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    public List<QuizQuestion> getAllQuizQuestions() {
        return quizQuestionRepository.findAll();
    }

    public Optional<QuizQuestion> getQuizQuestionById(Integer id) {
        return quizQuestionRepository.findById(id);
    }

    public QuizQuestion saveQuizQuestion(QuizQuestion quizQuestion) {
        return quizQuestionRepository.save(quizQuestion);
    }

    public void deleteQuizQuestion(Integer id) {
        quizQuestionRepository.deleteById(id);
    }
} 