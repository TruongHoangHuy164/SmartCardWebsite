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
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

        // Đảm bảo thời gian bắt đầu được lưu chính xác với múi giờ Việt Nam
        ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime vietnamTime = ZonedDateTime.now(vietnamZone);
        LocalDateTime startTime = vietnamTime.toLocalDateTime();
        attempt.setStartedAt(startTime);

        // Debug: In ra thời gian bắt đầu
        System.out.println("=== DEBUG: Tạo attempt mới ===");
        System.out.println("User: " + userId);
        System.out.println("Exam: " + exam.getTitle());
        System.out.println("StartedAt: " + startTime);
        System.out.println("=== END DEBUG ===");

        return Optional.of(examAttemptRepository.save(attempt));
    }

    @Transactional
    public Optional<ExamAttempt> submitAttempt(ExamAttempt attempt, Map<Long, Long> answers, Integer remainingTime) {
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
        // Đảm bảo thời gian nộp bài được lưu chính xác với múi giờ Việt Nam
        ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime vietnamTime = ZonedDateTime.now(vietnamZone);
        LocalDateTime submitTime = vietnamTime.toLocalDateTime();
        attempt.setSubmittedAt(submitTime);
        attempt.setScore(correctCount);
        attempt.setCorrectCount(correctCount);
        // Sửa logic lưu remainingTime: nếu lớn hơn 60 thì chia cho 60 (giây -> phút)
        if (remainingTime != null) {
            if (remainingTime > 60) {
                attempt.setRemainingTime(remainingTime / 60);
            } else {
                attempt.setRemainingTime(remainingTime);
            }
        } else {
            attempt.setRemainingTime(null);
        }

        // Debug: In ra thông tin submit
        System.out.println("=== DEBUG: Submit attempt ===");
        System.out.println("Attempt ID: " + attempt.getId());
        System.out.println("User: " + attempt.getUserId());
        System.out.println("StartedAt: " + attempt.getStartedAt());
        System.out.println("SubmittedAt: " + submitTime);
        System.out.println("Score: " + correctCount);
        System.out.println("=== END DEBUG ===");

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

    public java.util.List<ExamAttempt> getAttemptsByUser(String userId) {
        return examAttemptRepository.findAll().stream()
                .filter(a -> a.getUserId().equals(userId))
                .toList();
    }
}