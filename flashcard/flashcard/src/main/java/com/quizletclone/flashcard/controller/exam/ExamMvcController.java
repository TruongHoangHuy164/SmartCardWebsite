package com.quizletclone.flashcard.controller.exam;

import com.quizletclone.flashcard.model.exam.Exam;
import com.quizletclone.flashcard.service.exam.ExamService;
import com.quizletclone.flashcard.model.User;
import com.quizletclone.flashcard.service.exam.ExamQuestionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/exams")
public class ExamMvcController {
    @Autowired
    private ExamService examService;

    @Autowired
    private ExamQuestionService examQuestionService;

    @GetMapping
    public String listExams(Model model) {
        model.addAttribute("exams", examService.getPublicExams());
        return "exam/list";
    }

    @GetMapping("/create")
    public String createExamForm(Model model) {
        model.addAttribute("exam", new Exam());
        return "exam/create";
    }

    @PostMapping("/create")
    public String createExam(@ModelAttribute Exam exam, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null) {
            exam.setCreatedBy(user.getUsername());
        }
        examService.createExam(exam);
        return "redirect:/exams";
    }

    @GetMapping("/my")
    public String myExams(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null)
            return "redirect:/login";
        model.addAttribute("exams", examService.getExamsByCreator(user.getUsername()));
        return "exam/list";
    }

    @GetMapping("/{id}")
    public String examDetail(@PathVariable Long id, Model model) {
        Optional<Exam> exam = examService.getExamById(id);
        if (exam.isPresent()) {
            model.addAttribute("exam", exam.get());
            model.addAttribute("questions", examQuestionService.getQuestionsByExam(exam.get()));
            return "exam/detail";
        }
        return "error/404";
    }

    @GetMapping("/code/{code}")
    public String examDetailByCode(@PathVariable String code, Model model) {
        Optional<Exam> exam = examService.getExamByCode(code);
        if (exam.isPresent()) {
            model.addAttribute("exam", exam.get());
            model.addAttribute("questions", examQuestionService.getQuestionsByExam(exam.get()));
            return "exam/detail";
        }
        return "error/404";
    }

    @PostMapping("/toggle-public/{id}")
    public String togglePublic(@PathVariable Long id, @RequestParam(required = false) Boolean isPublic,
            HttpSession session) {
        Optional<Exam> examOpt = examService.getExamById(id);
        User user = (User) session.getAttribute("loggedInUser");
        if (examOpt.isEmpty() || user == null || !examOpt.get().getCreatedBy().equals(user.getUsername())) {
            return "error/404";
        }
        Exam exam = examOpt.get();
        exam.setPublic(isPublic != null && isPublic);
        examService.createExam(exam);
        return "redirect:/exams/" + id;
    }

    @GetMapping("/{id}/questions-count")
    @ResponseBody
    public Map<String, Object> getQuestionsCount(@PathVariable Long id) {
        Map<String, Object> res = new HashMap<>();
        Optional<Exam> examOpt = examService.getExamById(id);
        res.put("count", examOpt.map(e -> examQuestionService.getQuestionsByExam(e).size()).orElse(0));
        return res;
    }
}