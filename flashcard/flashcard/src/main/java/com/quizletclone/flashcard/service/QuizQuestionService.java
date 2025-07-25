package com.quizletclone.flashcard.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quizletclone.flashcard.model.QuizQuestion;
import com.quizletclone.flashcard.repository.QuizQuestionRepository;

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

    public List<QuizQuestion> getQuestionsByDeckId(Integer deckId) {
        return quizQuestionRepository.findByFlashcard_Deck_Id(deckId);
    }

    public Optional<QuizQuestion> getQuestionById(Integer id) {
        return quizQuestionRepository.findById(id);
    }

    public int countByDeckId(Integer deckId) {
        return quizQuestionRepository.countByFlashcard_Deck_Id(deckId);
    }

    public QuizQuestion updateQuestion(Integer id, QuizQuestion updatedQuestion) {
        return quizQuestionRepository.findById(id).map(question -> {
            question.setFlashcard(updatedQuestion.getFlashcard());
            question.setQuestionText(updatedQuestion.getQuestionText());
            question.setCorrectAnswer(updatedQuestion.getCorrectAnswer());
            return quizQuestionRepository.save(question);
        }).orElse(null);
    }

    public void deleteQuestion(Integer id) {
        quizQuestionRepository.deleteById(id);
    }
}