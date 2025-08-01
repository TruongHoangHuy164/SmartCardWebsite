package com.quizletclone.flashcard.controller.exam;

import com.quizletclone.flashcard.model.exam.Exam;
import com.quizletclone.flashcard.service.exam.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/exams/attempt")
public class ExamAttemptCodeController {

    @Autowired
    private ExamService examService;

    @GetMapping("/code")
    public String showCodeForm() {
        return "exam/attempt_code";
    }

    @PostMapping("/code")
    public String handleCodeSubmit(@RequestParam("examCode") String examCode, Model model) {
        var examOpt = examService.findByCode(examCode.trim());
        if (examOpt.isPresent()) {
            return "redirect:/exams/attempt/" + examOpt.get().getId();
        } else {
            model.addAttribute("error", "Mã đề thi không hợp lệ hoặc không tồn tại!");
            return "exam/attempt_code";
        }
    }
}
