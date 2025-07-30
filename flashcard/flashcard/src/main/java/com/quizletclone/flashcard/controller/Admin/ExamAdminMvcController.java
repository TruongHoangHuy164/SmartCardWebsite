package com.quizletclone.flashcard.controller.Admin;

import com.quizletclone.flashcard.model.exam.Exam;
import com.quizletclone.flashcard.service.exam.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ExamAdminMvcController {
    @Autowired
    private ExamService examService;

    @GetMapping("/admin/exams")
    public String listExams(Model model, HttpSession session) {
        // Có thể kiểm tra quyền admin ở đây nếu cần
        List<Exam> exams = examService.getAllExams();
        model.addAttribute("exams", exams);
        return "admin/exam_list";
    }
}
