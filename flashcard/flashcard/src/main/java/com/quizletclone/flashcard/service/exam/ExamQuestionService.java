package com.quizletclone.flashcard.service.exam;

import com.quizletclone.flashcard.model.exam.ExamQuestion;
import com.quizletclone.flashcard.model.exam.Exam;
import com.quizletclone.flashcard.repository.exam.ExamQuestionRepository;
import com.quizletclone.flashcard.service.exam.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExamQuestionService {
    @Autowired
    private ExamQuestionRepository examQuestionRepository;

    @Autowired
    private ExamService examService;

    public ExamQuestion addQuestion(ExamQuestion question) {
        ExamQuestion saved = examQuestionRepository.save(question);
        Exam exam = question.getExam();
        int count = examQuestionRepository.findAll().stream()
                .filter(q -> q.getExam().getId().equals(exam.getId()))
                .toList().size();
        exam.setTotalQuestions(count);
        examService.createExam(exam);
        return saved;
    }

    public List<ExamQuestion> getQuestionsByExam(Exam exam) {
        return examQuestionRepository.findAll().stream()
                .filter(q -> q.getExam().getId().equals(exam.getId()))
                .toList();
    }

    public Optional<ExamQuestion> getById(Long id) {
        return examQuestionRepository.findById(id);
    }

    public Optional<ExamQuestion> getByExamId(Long id) {
        return examQuestionRepository.findByExamId(id);
    }

    public void deleteQuestion(Long id) {
        Optional<ExamQuestion> questionOpt = examQuestionRepository.findById(id);
        if (questionOpt.isPresent()) {
            Exam exam = questionOpt.get().getExam();
            examQuestionRepository.deleteById(id);
            int count = examQuestionRepository.findAll().stream()
                    .filter(q -> q.getExam().getId().equals(exam.getId()))
                    .toList().size();
            exam.setTotalQuestions(count);
            examService.createExam(exam);
        }
    }
}