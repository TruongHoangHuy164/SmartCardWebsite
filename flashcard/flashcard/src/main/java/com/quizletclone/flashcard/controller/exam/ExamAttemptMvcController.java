package com.quizletclone.flashcard.controller.exam;

import com.quizletclone.flashcard.model.exam.Exam;
import com.quizletclone.flashcard.model.exam.ExamAttempt;
import com.quizletclone.flashcard.service.exam.ExamAttemptService;
import com.quizletclone.flashcard.service.exam.ExamService;
import com.quizletclone.flashcard.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@Controller
@RequestMapping("/exams/attempt")
public class ExamAttemptMvcController {
    @Autowired
    private ExamService examService;
    @Autowired
    private ExamAttemptService examAttemptService;

    @GetMapping("/{examId}")
    public String startAttempt(@PathVariable Long examId, Model model, HttpSession session) {
        Optional<Exam> examOpt = examService.getExamById(examId);
        if (examOpt.isEmpty()) {
            System.out.println("Không tìm thấy đề thi với id: " + examId);
            return "error/404";
        }
        Exam exam = examOpt.get();
        if (exam.getTotalQuestions() == 0) {
            System.out.println("Đề thi không có câu hỏi");
            return "redirect:/exams/" + examId;
        }
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            System.out.println("Chưa đăng nhập");
            return "redirect:/login";
        }
        // Kiểm tra đã có attempt chưa, nếu có thì dùng lại, nếu chưa thì tạo mới
        Optional<ExamAttempt> attemptOpt = examAttemptService.getByIdForUserAndExam(user.getUsername(), examId);
        ExamAttempt attempt;
        if (attemptOpt.isPresent()) {
            attempt = attemptOpt.get();
            System.out.println("Đã có attempt cũ: " + attempt.getId());
        } else {
            Optional<ExamAttempt> created = examAttemptService.createAttempt(exam, user.getUsername());
            if (created.isEmpty()) {
                System.out.println("Không tạo được attempt mới");
                return "error/404";
            }
            attempt = created.get();
            System.out.println("Tạo attempt mới: " + attempt.getId());
        }
        model.addAttribute("exam", exam);
        model.addAttribute("attempt", attempt);
        return "exam/attempt_start";
    }

    @PostMapping("/{attemptId}/submit")
    public String submitAttempt(@PathVariable Long attemptId,
            @RequestParam Map<String, String> answers,
            @RequestParam(required = false) Integer remainingTime,
            HttpSession session) {
        Optional<ExamAttempt> attemptOpt = examAttemptService.getById(attemptId);
        if (attemptOpt.isEmpty()) {
            return "error/404";
        }
        ExamAttempt attempt = attemptOpt.get();
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !attempt.getUserId().equals(user.getUsername())) {
            return "redirect:/login";
        }
        // Xử lý câu trả lời
        Map<Long, Long> processedAnswers = new HashMap<>();
        answers.forEach((key, value) -> {
            if (key.startsWith("answer_")) {
                Long questionId = Long.parseLong(key.substring(7));
                Long optionId = Long.parseLong(value);
                processedAnswers.put(questionId, optionId);
            }
        });
        Optional<ExamAttempt> submittedAttempt = examAttemptService.submitAttempt(attempt, processedAnswers,
                remainingTime);
        if (submittedAttempt.isEmpty()) {
            return "error/404";
        }
        return "redirect:/exams/attempt/" + attemptId + "/result";
    }

    @GetMapping("/{attemptId}/result")
    public String showResult(@PathVariable Long attemptId, Model model, HttpSession session) {
        Optional<ExamAttempt> attemptOpt = examAttemptService.getById(attemptId);
        if (attemptOpt.isEmpty()) {
            return "error/404";
        }
        ExamAttempt attempt = attemptOpt.get();
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !attempt.getUserId().equals(user.getUsername())) {
            return "redirect:/login";
        }
        model.addAttribute("attempt", attempt);
        model.addAttribute("exam", attempt.getExam());
        model.addAttribute("correctCount", attempt.getCorrectCount());
        model.addAttribute("total", attempt.getExam().getTotalQuestions());
        // Nếu có service lấy danh sách kết quả từng câu hỏi:
        // model.addAttribute("results",
        // examAttemptAnswerService.getAnswersByAttempt(attempt));
        return "exam/attempt_result";
    }

    @GetMapping("/history")
    public String attemptHistory(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        var attempts = examAttemptService.getAttemptsByUser(user.getUsername());
        model.addAttribute("attempts", attempts);
        return "exam/attempt_history";
    }
}