package com.quizletclone.flashcard.service.exam;

import com.quizletclone.flashcard.model.exam.ExamAttemptAnswer;
import com.quizletclone.flashcard.model.exam.ExamAttempt;
import com.quizletclone.flashcard.repository.exam.ExamAttemptAnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamAttemptAnswerService {
    @Autowired
    private ExamAttemptAnswerRepository examAttemptAnswerRepository;

    public ExamAttemptAnswer saveAnswer(ExamAttemptAnswer answer) {
        return examAttemptAnswerRepository.save(answer);
    }

    public List<ExamAttemptAnswer> getAnswersByAttempt(ExamAttempt attempt) {
        return examAttemptAnswerRepository.findAll().stream()
                .filter(a -> a.getAttempt().getId().equals(attempt.getId()))
                .toList();
    }
}