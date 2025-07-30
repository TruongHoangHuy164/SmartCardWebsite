package com.quizletclone.flashcard.controller.Admin;

import com.quizletclone.flashcard.model.exam.Exam;
import com.quizletclone.flashcard.service.exam.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/exams")
public class ExamAdminController {
    @Autowired
    private ExamService examService;

    @GetMapping
    public List<Exam> getAllExams() {
        return examService.getPublicExams(); // hoặc getAllExams() nếu muốn lấy tất cả
    }

    @GetMapping("/{id}")
    public Exam getExamById(@PathVariable Long id) {
        Optional<Exam> exam = examService.getExamById(id);
        return exam.orElse(null);
    }

    @PostMapping
    public Exam createExam(@RequestBody Exam exam) {
        return examService.createExam(exam);
    }

    @PutMapping("/{id}")
    public Exam updateExam(@PathVariable Long id, @RequestBody Exam exam) {
        exam.setId(id);
        return examService.createExam(exam);
    }

    @DeleteMapping("/{id}")
    public void deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
    }
}