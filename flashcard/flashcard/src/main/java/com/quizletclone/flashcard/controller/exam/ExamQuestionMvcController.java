package com.quizletclone.flashcard.controller.exam;

import com.quizletclone.flashcard.model.exam.Exam;
import com.quizletclone.flashcard.model.exam.ExamQuestion;
import com.quizletclone.flashcard.model.User;
import com.quizletclone.flashcard.service.exam.ExamQuestionService;
import com.quizletclone.flashcard.service.exam.ExamService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/exams/questions")
public class ExamQuestionMvcController {
    private static final Logger logger = LoggerFactory.getLogger(ExamQuestionMvcController.class);
    @Autowired
    private ExamQuestionService examQuestionService;
    @Autowired
    private ExamService examService;

    @GetMapping("/add/{examId}")
    public String addQuestionForm(@PathVariable Long examId, Model model, HttpSession session) {
        Optional<Exam> examOpt = examService.getExamById(examId);
        User user = (User) session.getAttribute("loggedInUser");
        if (examOpt.isEmpty() || user == null || !examOpt.get().getCreatedBy().equals(user.getUsername())) {
            return "error/404";
        }
        model.addAttribute("question", new ExamQuestion());
        model.addAttribute("examId", examId);
        return "exam/question_add";
    }

    @PostMapping("/add/{examId}")
    public String addQuestion(@PathVariable Long examId, @ModelAttribute ExamQuestion question, HttpSession session) {
        Optional<Exam> examOpt = examService.getExamById(examId);
        User user = (User) session.getAttribute("loggedInUser");
        if (examOpt.isEmpty() || user == null || !examOpt.get().getCreatedBy().equals(user.getUsername())) {
            return "error/404";
        }
        question.setExam(examOpt.get());
        examQuestionService.addQuestion(question);
        return "redirect:/exams/questions/list/" + examId;
    }

    @GetMapping("/list/{examId}")
    public String listQuestions(@PathVariable Long examId, Model model, HttpSession session) {
        Optional<Exam> examOpt = examService.getExamById(examId);
        if (examOpt.isPresent()) {
            Exam exam = examOpt.get();
            model.addAttribute("exam", exam);
            model.addAttribute("questions", examQuestionService.getQuestionsByExam(exam));
            model.addAttribute("examId", examId);
            return "exam/question_list";
        } else {
            logger.error("Không tìm thấy đề thi với id: {}", examId);
            model.addAttribute("error", "Không tìm thấy đề thi hoặc bạn không có quyền truy cập.");
            return "error/404";
        }
    }

    @GetMapping("/edit/{questionId}")
    public String editQuestionForm(@PathVariable Long questionId, Model model, HttpSession session) {
        Optional<ExamQuestion> questionOpt = examQuestionService.getById(questionId);
        if (questionOpt.isEmpty())
            return "error/404";
        ExamQuestion question = questionOpt.get();
        Exam exam = question.getExam();
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !exam.getCreatedBy().equals(user.getUsername()))
            return "error/404";
        model.addAttribute("question", question);
        model.addAttribute("examId", exam.getId());
        return "exam/question_add";
    }

    @PostMapping("/edit/{questionId}")
    public String editQuestion(@PathVariable Long questionId, @ModelAttribute ExamQuestion question,
            HttpSession session) {
        Optional<ExamQuestion> oldOpt = examQuestionService.getById(questionId);
        if (oldOpt.isEmpty())
            return "error/404";
        ExamQuestion old = oldOpt.get();
        Exam exam = old.getExam();
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !exam.getCreatedBy().equals(user.getUsername()))
            return "error/404";
        old.setContent(question.getContent());
        examQuestionService.addQuestion(old);
        return "redirect:/exams/questions/list/" + exam.getId();
    }

    @GetMapping("/delete/{questionId}")
    public String deleteQuestion(@PathVariable Long questionId, HttpSession session) {
        Optional<ExamQuestion> questionOpt = examQuestionService.getById(questionId);
        if (questionOpt.isEmpty())
            return "error/404";
        ExamQuestion question = questionOpt.get();
        Exam exam = question.getExam();
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !exam.getCreatedBy().equals(user.getUsername()))
            return "error/404";
        examQuestionService.deleteQuestion(questionId);
        return "redirect:/exams/questions/list/" + exam.getId();
    }

    @PostMapping("/add-inline/{examId}")
    public String addQuestionInline(@PathVariable Long examId, @RequestParam String content, HttpSession session) {
        Optional<Exam> examOpt = examService.getExamById(examId);
        User user = (User) session.getAttribute("loggedInUser");
        if (examOpt.isEmpty() || user == null || !examOpt.get().getCreatedBy().equals(user.getUsername())) {
            return "error/404";
        }
        ExamQuestion question = new ExamQuestion();
        question.setExam(examOpt.get());
        question.setContent(content);
        examQuestionService.addQuestion(question);
        return "redirect:/exams/" + examId;
    }

    @PostMapping("/edit-inline/{questionId}")
    public String editQuestionInline(@PathVariable Long questionId, @RequestParam String content, HttpSession session) {
        Optional<ExamQuestion> oldOpt = examQuestionService.getById(questionId);
        if (oldOpt.isEmpty())
            return "error/404";
        ExamQuestion old = oldOpt.get();
        Exam exam = old.getExam();
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !exam.getCreatedBy().equals(user.getUsername()))
            return "error/404";
        old.setContent(content);
        examQuestionService.addQuestion(old);
        return "redirect:/exams/" + exam.getId();
    }

    @PostMapping("/delete-inline/{questionId}")
    public String deleteQuestionInline(@PathVariable Long questionId, HttpSession session) {
        Optional<ExamQuestion> questionOpt = examQuestionService.getById(questionId);
        if (questionOpt.isEmpty())
            return "error/404";
        ExamQuestion question = questionOpt.get();
        Exam exam = question.getExam();
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !exam.getCreatedBy().equals(user.getUsername()))
            return "error/404";
        examQuestionService.deleteQuestion(questionId);
        return "redirect:/exams/" + exam.getId();
    }
}