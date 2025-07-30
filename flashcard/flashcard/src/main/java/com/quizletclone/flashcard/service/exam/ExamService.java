package com.quizletclone.flashcard.service.exam;

import com.quizletclone.flashcard.model.exam.Exam;
import com.quizletclone.flashcard.repository.exam.ExamRepository;
import com.quizletclone.flashcard.repository.exam.ExamAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ExamService {
    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }
    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    public Exam createExam(Exam exam) {
        if (exam.getCode() == null || exam.getCode().isEmpty()) {
            while (true) {
                String code = generateRandomCode(8);
                boolean exists = examRepository.findAll().stream()
                        .anyMatch(e -> e.getCode() != null && e.getCode().equals(code));
                if (!exists) {
                    exam.setCode(code);
                    break;
                }
            }
        }
        return examRepository.save(exam);
    }

    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public List<Exam> getPublicExams() {
        return examRepository.findAll().stream().filter(Exam::isPublic).toList();
    }

    public Optional<Exam> getExamById(Long id) {
        return examRepository.findById(id);
    }

    public Optional<Exam> getExamByCode(String code) {
        return examRepository.findByCode(code);
    }

    public void increaseAttemptCount(Long examId) {
        examRepository.findById(examId).ifPresent(exam -> {
            exam.setTotalAttempts(exam.getTotalAttempts() + 1);
            examRepository.save(exam);
        });
    }

    public List<Exam> getExamsByCreator(String username) {
        return examRepository.findAll().stream()
                .filter(e -> username.equals(e.getCreatedBy()))
                .toList();
    }

    @Transactional
    public void deleteExam(Long id) {
        // Xóa tất cả các attempt liên quan trước
        examAttemptRepository.deleteByExam_Id(id);
        examRepository.deleteById(id);
    }
}