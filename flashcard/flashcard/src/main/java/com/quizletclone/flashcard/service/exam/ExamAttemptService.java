package com.quizletclone.flashcard.service.exam;

import com.quizletclone.flashcard.model.exam.Exam;
import com.quizletclone.flashcard.model.exam.ExamAttempt;
import com.quizletclone.flashcard.model.exam.ExamQuestion;
import com.quizletclone.flashcard.model.exam.ExamOption;
import com.quizletclone.flashcard.model.exam.ExamAttemptAnswer;
import com.quizletclone.flashcard.repository.exam.ExamAttemptRepository;
import com.quizletclone.flashcard.service.exam.ExamQuestionService;
import com.quizletclone.flashcard.service.exam.ExamOptionService;
import com.quizletclone.flashcard.service.exam.ExamAttemptAnswerService;
import com.quizletclone.flashcard.service.exam.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class ExamAttemptService {
    @Autowired
    private ExamAttemptRepository examAttemptRepository;
    @Autowired
    private ExamQuestionService examQuestionService;
    @Autowired
    private ExamOptionService examOptionService;
    @Autowired
    private ExamAttemptAnswerService examAttemptAnswerService;
    @Autowired
    private ExamService examService;

    public Optional<ExamAttempt> getById(Long id) {
        return examAttemptRepository.findById(id);
    }

    public Optional<ExamAttempt> getByIdForUserAndExam(String userId, Long examId) {
        return examAttemptRepository.findAll().stream()
                .filter(a -> a.getUserId().equals(userId) && a.getExam().getId().equals(examId))
                .findFirst();
    }

    @Transactional
    public Optional<ExamAttempt> createAttempt(Exam exam, String userId) {
        if (exam == null || userId == null) {
            return Optional.empty();
        }

        ExamAttempt attempt = new ExamAttempt();
        attempt.setExam(exam);
        attempt.setUserId(userId);
        attempt.setStartedAt(LocalDateTime.now());

        return Optional.of(examAttemptRepository.save(attempt));
    }

    @Transactional
    public Optional<ExamAttempt> submitAttempt(ExamAttempt attempt, Map<Long, Long> answers) {
        if (attempt == null || answers == null) {
            return Optional.empty();
        }
        int correctCount = 0;
        for (ExamQuestion q : examQuestionService.getQuestionsByExam(attempt.getExam())) {
            Long selectedOptionId = answers.get(q.getId());
            if (selectedOptionId != null) {
                ExamOption selectedOption = examOptionService.getById(selectedOptionId).orElse(null);
                boolean isCorrect = selectedOption != null && selectedOption.isCorrect();
                if (isCorrect)
                    correctCount++;
            }
        }
        attempt.setSubmittedAt(java.time.LocalDateTime.now());
        attempt.setScore(correctCount);
        attempt.setCorrectCount(correctCount);
        // Tăng số lượt làm bài
        Exam exam = attempt.getExam();
        Integer attempts = exam.getTotalAttempts();
        exam.setTotalAttempts(attempts == null ? 1 : attempts + 1);
        examService.createExam(exam);
        return Optional.of(examAttemptRepository.save(attempt));
    }

    public java.util.List<ExamAttempt> getAttemptsByExam(Exam exam) {
        return examAttemptRepository.findAll().stream()
                .filter(a -> a.getExam().getId().equals(exam.getId()))
                .toList();
    }
}