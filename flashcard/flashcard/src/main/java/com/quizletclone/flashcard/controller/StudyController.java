package com.quizletclone.flashcard.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizletclone.flashcard.model.Deck;
import com.quizletclone.flashcard.model.Quiz;
import com.quizletclone.flashcard.model.QuizQuestion;
import com.quizletclone.flashcard.model.QuizQuestionResult;
import com.quizletclone.flashcard.model.User;
import com.quizletclone.flashcard.service.DeckService;
import com.quizletclone.flashcard.service.QuizQuestionResultService;
import com.quizletclone.flashcard.service.QuizQuestionService;
import com.quizletclone.flashcard.service.QuizService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/study")
public class StudyController {

    @Autowired
    private DeckService deckService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuizQuestionService quizQuestionService;

    @Autowired
    private QuizQuestionResultService quizQuestionResultService;

    @GetMapping("/deck/{deckId}")
    public String studyDeck(@PathVariable Integer deckId,
            HttpSession session,
            Model model) throws JsonProcessingException {

        System.out.println("➡️ Bắt đầu xử lý studyDeck với deckId = " + deckId);

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            System.out.println("❌ Người dùng chưa đăng nhập, chuyển hướng đến trang đăng nhập.");
            return "redirect:/login";
        }
        System.out.println("✅ Người dùng đăng nhập: " + user.getUsername());

        Deck deck = deckService.getDeckById(deckId)
                .orElseThrow(() -> {
                    System.out.println("❌ Không tìm thấy Deck với ID: " + deckId);
                    return new IllegalArgumentException("Deck không tồn tại");
                });
        System.out.println("✅ Tìm thấy Deck: " + deck.getTitle());

        Quiz quiz = new Quiz();
        quiz.setUser(user);
        quiz.setDeck(deck);
        quiz.setCreatedAt(new Date());

        quiz = quizService.saveQuiz(quiz); // lưu vào DB
        System.out.println("✅ Đã tạo quiz với ID: " + quiz.getId());

        List<QuizQuestion> questions = quizQuestionService.getQuestionsByDeckId(deckId);
        System.out.println("✅ Số lượng câu hỏi trong quiz: " + questions.size());

        ObjectMapper mapper = new ObjectMapper();
        String questionsJson = mapper.writeValueAsString(questions);

        model.addAttribute("quizQuestionsJson", questionsJson); // ✅ Sử dụng JSON string
        model.addAttribute("quizId", quiz.getId());

        System.out.println("➡️ Chuyển đến view: study/deck");
        return "study/deck"; // Giao diện học
    }

    @PostMapping("/complete")
    public ResponseEntity<?> completeQuiz(@RequestParam Integer quizId,
            @RequestParam Float score,
            @RequestParam Integer correctAnswers,
            @RequestParam Integer totalQuestions) {
        Optional<Quiz> quizOpt = quizService.getQuizById(quizId);
        if (quizOpt.isEmpty())
            return ResponseEntity.notFound().build();

        Quiz quiz = quizOpt.get();
        quiz.setScore(score);
        quiz.setCorrectAnswers(correctAnswers);
        quiz.setTotalQuestions(totalQuestions);

        quizService.saveQuiz(quiz);

        // Trả về URL để client redirect
        return ResponseEntity.ok("/decks");
    }

    @PostMapping("/save-result")
    @ResponseBody
    public ResponseEntity<?> saveQuestionResult(
            @RequestParam Integer questionId,
            @RequestParam String userAnswer,
            @RequestParam boolean isCorrect) {

        Optional<QuizQuestion> questionOpt = quizQuestionService.getQuestionById(questionId);

        if (questionOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Câu hỏi không tồn tại.");
        }

        QuizQuestionResult result = new QuizQuestionResult();
        result.setQuizQuestion(questionOpt.get());
        result.setUserAnswer(userAnswer);
        result.setIsCorrect(isCorrect);

        quizQuestionResultService.saveResult(result); // sử dụng service

        return ResponseEntity.ok("Đã lưu kết quả.");
    }

}
