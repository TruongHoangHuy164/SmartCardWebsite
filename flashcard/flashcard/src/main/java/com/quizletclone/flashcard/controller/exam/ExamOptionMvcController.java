package com.quizletclone.flashcard.controller.exam;

import com.quizletclone.flashcard.model.exam.ExamOption;
import com.quizletclone.flashcard.model.exam.ExamQuestion;
import com.quizletclone.flashcard.model.User;
import com.quizletclone.flashcard.model.exam.Exam;
import com.quizletclone.flashcard.service.exam.ExamOptionService;
import com.quizletclone.flashcard.service.exam.ExamQuestionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/exams/options")
public class ExamOptionMvcController {
    @Autowired
    private ExamOptionService examOptionService;
    @Autowired
    private ExamQuestionService examQuestionService;

    @GetMapping("/add/{questionId}")
    public String addOptionForm(@PathVariable Long questionId, Model model, HttpSession session) {
        Optional<ExamQuestion> questionOpt = examQuestionService.getById(questionId);
        if (questionOpt.isEmpty())
            return "error/404";
        ExamQuestion question = questionOpt.get();
        Exam exam = question.getExam();
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !exam.getCreatedBy().equals(user.getUsername()))
            return "error/404";
        model.addAttribute("option", new ExamOption());
        model.addAttribute("questionId", questionId);
        return "exam/option_add";
    }

    @PostMapping("/add/{questionId}")
    public String addOption(@PathVariable Long questionId, @ModelAttribute ExamOption option, HttpSession session) {
        Optional<ExamQuestion> questionOpt = examQuestionService.getById(questionId);
        if (questionOpt.isEmpty())
            return "error/404";
        ExamQuestion question = questionOpt.get();
        Exam exam = question.getExam();
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !exam.getCreatedBy().equals(user.getUsername()))
            return "error/404";
        option.setQuestion(question);
        examOptionService.addOption(option);
        return "redirect:/exams/options/list/" + questionId;
    }

    @GetMapping("/list/{questionId}")
    public String listOptions(@PathVariable Long questionId, Model model, HttpSession session) {
        Optional<ExamQuestion> questionOpt = examQuestionService.getById(questionId);
        if (questionOpt.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy câu hỏi.");
            return "error/404";
        }
        ExamQuestion question = questionOpt.get();
        Exam exam = question.getExam();
        model.addAttribute("question", question);
        model.addAttribute("exam", exam);
        model.addAttribute("options", examOptionService.getOptionsByQuestion(question));
        model.addAttribute("questionId", questionId);
        return "exam/option_list";
    }

    @GetMapping("/edit/{optionId}")
    public String editOptionForm(@PathVariable Long optionId, Model model, HttpSession session) {
        Optional<ExamOption> optionOpt = examOptionService.getById(optionId);
        if (optionOpt.isEmpty())
            return "error/404";
        ExamOption option = optionOpt.get();
        ExamQuestion question = option.getQuestion();
        Exam exam = question.getExam();
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !exam.getCreatedBy().equals(user.getUsername()))
            return "error/404";
        model.addAttribute("option", option);
        model.addAttribute("questionId", question.getId());
        model.addAttribute("question", question);
        model.addAttribute("exam", exam);
        return "exam/option_add";
    }

    @PostMapping("/edit/{optionId}")
    public String editOption(@PathVariable Long optionId, @ModelAttribute ExamOption option, HttpSession session) {
        Optional<ExamOption> oldOpt = examOptionService.getById(optionId);
        if (oldOpt.isEmpty())
            return "error/404";
        ExamOption old = oldOpt.get();
        ExamQuestion question = old.getQuestion();
        Exam exam = question.getExam();
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !exam.getCreatedBy().equals(user.getUsername()))
            return "error/404";
        old.setContent(option.getContent());
        old.setCorrect(option.isCorrect());
        examOptionService.addOption(old);
        return "redirect:/exams/options/list/" + question.getId();
    }

    @GetMapping("/delete/{optionId}")
    public String deleteOption(@PathVariable Long optionId, HttpSession session) {
        Optional<ExamOption> optionOpt = examOptionService.getById(optionId);
        if (optionOpt.isEmpty())
            return "error/404";
        ExamOption option = optionOpt.get();
        ExamQuestion question = option.getQuestion();
        Exam exam = question.getExam();
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !exam.getCreatedBy().equals(user.getUsername()))
            return "error/404";
        examOptionService.deleteOption(optionId);
        return "redirect:/exams/options/list/" + question.getId();
    }

    @PostMapping("/add-inline/{questionId}")
    public String addOptionInline(@PathVariable Long questionId, @RequestParam String content,
            @RequestParam(required = false) boolean correct, HttpSession session) {
        Optional<ExamQuestion> questionOpt = examQuestionService.getById(questionId);
        if (questionOpt.isEmpty())
            return "error/404";
        ExamQuestion question = questionOpt.get();
        Exam exam = question.getExam();
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !exam.getCreatedBy().equals(user.getUsername()))
            return "error/404";
        ExamOption option = new ExamOption();
        option.setQuestion(question);
        option.setContent(content);
        option.setCorrect(correct);
        examOptionService.addOption(option);
        return "redirect:/exams/" + exam.getId();
    }

    @PostMapping("/edit-inline/{optionId}")
    public String editOptionInline(@PathVariable Long optionId, @RequestParam String content,
            @RequestParam(required = false) boolean correct, HttpSession session) {
        Optional<ExamOption> oldOpt = examOptionService.getById(optionId);
        if (oldOpt.isEmpty())
            return "error/404";
        ExamOption old = oldOpt.get();
        ExamQuestion question = old.getQuestion();
        Exam exam = question.getExam();
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !exam.getCreatedBy().equals(user.getUsername()))
            return "error/404";
        old.setContent(content);
        old.setCorrect(correct);
        examOptionService.addOption(old);
        return "redirect:/exams/" + exam.getId();
    }

    @PostMapping("/delete-inline/{optionId}")
    public String deleteOptionInline(@PathVariable Long optionId, HttpSession session) {
        Optional<ExamOption> optionOpt = examOptionService.getById(optionId);
        if (optionOpt.isEmpty())
            return "error/404";
        ExamOption option = optionOpt.get();
        ExamQuestion question = option.getQuestion();
        Exam exam = question.getExam();
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !exam.getCreatedBy().equals(user.getUsername()))
            return "error/404";
        examOptionService.deleteOption(optionId);
        return "redirect:/exams/" + exam.getId();
    }
}