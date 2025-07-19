package com.quizletclone.flashcard.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizletclone.flashcard.model.Deck;
import com.quizletclone.flashcard.model.Flashcard;
import com.quizletclone.flashcard.model.QuizQuestion;
import com.quizletclone.flashcard.repository.DeckRepository;
import com.quizletclone.flashcard.repository.FlashcardRepository;
import com.quizletclone.flashcard.repository.QuizQuestionRepository;
import com.quizletclone.flashcard.repository.QuizRepository;
import com.quizletclone.flashcard.service.AIService;

@Controller
@RequestMapping("/quiz-questions")
public class QuizQuestionViewController {

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private FlashcardRepository flashcardRepository;

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private AIService openAiService;

    // Danh sách tất cả câu hỏi
    @GetMapping("/deck/{deckId}")
    public String getQuestionsByDeck(@PathVariable Integer deckId, Model model) {
        Deck deck = deckRepository.findById(deckId).orElseThrow(() -> new RuntimeException("Deck not found"));

        List<QuizQuestion> quizQuestions = quizQuestionRepository.findByFlashcard_Deck_Id(deckId);

        model.addAttribute("deck", deck);
        model.addAttribute("quizQuestions", quizQuestions);

        // Lấy flashcard đầu tiên (nếu có)
        if (!quizQuestions.isEmpty()) {
            QuizQuestion qq = quizQuestions.get(0);
            if (qq.getFlashcard() != null) {
                model.addAttribute("firstFlashcardId", qq.getFlashcard().getId());
            }
        }

        return "quiz_question/deck_questions";
    }

    // Form thêm mới
    @GetMapping("/add/{id}")
    public String showAddForm(@PathVariable("id") Integer flashcardId, Model model) {
        Optional<Flashcard> optional = flashcardRepository.findById(flashcardId);
        if (optional.isEmpty()) {
            return "redirect:/decks";
        }

        Flashcard flashcard = optional.get();

        // Gợi ý từ AI
        String prompt = "Tạo 1 câu hỏi trắc nghiệm bằng dựa trên thuật ngữ '" + flashcard.getTerm() +
                "' và định nghĩa: '" + flashcard.getDefinition() +
                "'. Hãy trả lời bằng tiếng Việt dưới dạng JSON: {\"questionText\": \"...\", \"correctAnswer\": \"...\"}";

        String aiResponseJson = openAiService.ask(prompt); // bạn cần viết hàm này
        ObjectMapper objectMapper = new ObjectMapper();

        String questionText = "";
        String correctAnswer = "";
        try {
            JsonNode jsonNode = objectMapper.readTree(aiResponseJson);
            questionText = jsonNode.get("questionText").asText();
            correctAnswer = jsonNode.get("correctAnswer").asText();
        } catch (Exception e) {
            // Nếu lỗi thì để trống
        }

        model.addAttribute("flashcard", flashcard);
        model.addAttribute("suggestedQuestionText", questionText);
        model.addAttribute("suggestedCorrectAnswer", correctAnswer);

        return "quiz_question/add";
    }

    // Xử lý lưu
    @PostMapping("/save")
    public String saveQuestion(@RequestParam Integer flashcardId,
            @RequestParam String questionText,
            @RequestParam String correctAnswer) {

        Flashcard flashcard = flashcardRepository.findById(flashcardId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy flashcard"));

        QuizQuestion qq = new QuizQuestion();
        qq.setFlashcard(flashcard);
        qq.setQuestionText(questionText);
        qq.setCorrectAnswer(correctAnswer);
        // Chưa gán quiz (để null nếu chưa khởi tạo quiz)
        quizQuestionRepository.save(qq);

        return "redirect:/quiz-questions/deck/" + flashcard.getDeck().getId();
    }

    // Form sửa
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        QuizQuestion question = quizQuestionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID không hợp lệ: " + id));
        model.addAttribute("quizQuestion", question);
        model.addAttribute("quizzes", quizRepository.findAll());
        model.addAttribute("flashcards", flashcardRepository.findAll());
        return "quiz_question/edit";
    }

    // Xử lý cập nhật
    @PostMapping("/edit")
    public String updateQuestion(@ModelAttribute QuizQuestion quizQuestion) {
        quizQuestionRepository.save(quizQuestion);
        return "redirect:/quiz-questions";
    }

    // Xoá
    @GetMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable Integer id) {
        quizQuestionRepository.deleteById(id);
        return "redirect:/quiz-questions";
    }
}
