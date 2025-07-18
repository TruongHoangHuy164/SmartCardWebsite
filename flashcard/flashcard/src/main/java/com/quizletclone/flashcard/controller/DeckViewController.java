package com.quizletclone.flashcard.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.quizletclone.flashcard.model.Deck;
import com.quizletclone.flashcard.model.User;
import com.quizletclone.flashcard.service.DeckService;
import com.quizletclone.flashcard.service.QuizQuestionService;
import com.quizletclone.flashcard.service.QuizService;
import static com.quizletclone.flashcard.util.UrlHelper.redirectWithMessage;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/decks")
@RequiredArgsConstructor
public class DeckViewController {
    private final DeckService deckService;
    private final QuizService quizService;
    private final QuizQuestionService quizQuestionService;

    @GetMapping
    public String listDecks(Model model, HttpSession session) {
        try {
            List<Deck> decks = deckService.getAllDecks();
            model.addAttribute("decks", decks);

            User user = (User) session.getAttribute("loggedInUser");

            Map<Integer, Integer> totalQuestionsMap = new HashMap<>();
            Map<Integer, Integer> recentCorrectCountMap = new HashMap<>();
            Map<Integer, Integer> needReviewMap = new HashMap<>(); // ⬅ Thêm map này

            for (Deck deck : decks) {
                int totalQuestions = quizQuestionService.countByDeckId(deck.getId());
                totalQuestionsMap.put(deck.getId(), totalQuestions);

                int correctCount = 0;

                if (user != null) {
                    correctCount = quizService.getCorrectAnswersFromLatestQuiz(user.getId(), deck.getId());
                    recentCorrectCountMap.put(deck.getId(), correctCount);
                }

                recentCorrectCountMap.put(deck.getId(), correctCount);
                needReviewMap.put(deck.getId(), totalQuestions - correctCount); // ⬅ Tính luôn ở đây
            }

            model.addAttribute("totalQuestionsMap", totalQuestionsMap);
            model.addAttribute("recentCorrectCountMap", recentCorrectCountMap);
            model.addAttribute("needReviewMap", needReviewMap); // ⬅ Đưa vào model

            return "deck/list";
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi tải danh sách bộ thẻ: " + e.getMessage());
            return "deck/list";
        }
    }

    @GetMapping("/search")
    public String searchDecks(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String sub,
            Model model, HttpSession session) {
        List<Deck> decks = deckService.searchAndSortDecks(keyword, sort, sub);
        model.addAttribute("decks", decks);

        User user = (User) session.getAttribute("loggedInUser");

        Map<Integer, Integer> totalQuestionsMap = new HashMap<>();
        Map<Integer, Integer> recentCorrectCountMap = new HashMap<>();
        Map<Integer, Integer> needReviewMap = new HashMap<>(); // ⬅ Thêm map này

        for (Deck deck : decks) {
            int totalQuestions = quizQuestionService.countByDeckId(deck.getId());
            totalQuestionsMap.put(deck.getId(), totalQuestions);

            int correctCount = 0;

            if (user != null) {
                correctCount = quizService.getCorrectAnswersFromLatestQuiz(user.getId(), deck.getId());
                recentCorrectCountMap.put(deck.getId(), correctCount);
            }

            recentCorrectCountMap.put(deck.getId(), correctCount);
            needReviewMap.put(deck.getId(), totalQuestions - correctCount); // ⬅ Tính luôn ở đây
        }

        model.addAttribute("totalQuestionsMap", totalQuestionsMap);
        model.addAttribute("recentCorrectCountMap", recentCorrectCountMap);
        model.addAttribute("needReviewMap", needReviewMap); // ⬅ Đưa vào model

        return "deck/list :: deckList"; // ⚠️ chỉ trả về fragment
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session,
            RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return redirectWithMessage("/login", redirectAttributes, "error", "Vui lòng đăng nhập để tạo bộ thẻ.");
        }
        model.addAttribute("deck", new Deck());
        return "deck/add";
    }

    @PostMapping("/create")
    public String createDeck(@ModelAttribute Deck deck,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            if (loggedInUser == null) {
                return redirectWithMessage("/login", redirectAttributes, "error", "Vui lòng đăng nhập để tạo bộ thẻ.");
            }

            if (deck.getIsPublic() == null) {
                deck.setIsPublic(false);
            }
            deck.setUser(loggedInUser);
            deck.setCreatedAt(new Date());
            deckService.saveDeck(deck);
            return redirectWithMessage("/decks", redirectAttributes, "success", "Bộ thẻ đã được tạo thành công!");
        } catch (Exception e) {
            return redirectWithMessage("/decks", redirectAttributes, "error",
                    "Có lỗi xảy ra khi tạo bộ thẻ: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public String viewDeck(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Deck> deckOpt = deckService.getDeckById(id);
            if (deckOpt.isPresent()) {
                model.addAttribute("deck", deckOpt.get());
                return "deck/detail";
            } else {
                return redirectWithMessage("/decks", redirectAttributes, "error", "Không tìm thấy bộ thẻ!");
            }
        } catch (Exception e) {
            return redirectWithMessage("/decks", redirectAttributes, "error", "Có lỗi xảy ra: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/edit")
    public String editDeck(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Deck> deckOpt = deckService.getDeckById(id);
            if (deckOpt.isPresent()) {
                model.addAttribute("deck", deckOpt.get());
                return "deck/edit";
            } else {
                return redirectWithMessage("/decks", redirectAttributes, "error", "Không tìm thấy bộ thẻ!");
            }
        } catch (Exception e) {
            return redirectWithMessage("/decks", redirectAttributes, "error", "Có lỗi xảy ra: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/edit")
    public String updateDeck(@PathVariable Integer id,
            @ModelAttribute Deck deck,
            RedirectAttributes redirectAttributes) {
        try {
            Optional<Deck> existingDeckOpt = deckService.getDeckById(id);
            if (existingDeckOpt.isEmpty()) {
                return redirectWithMessage("/decks", redirectAttributes, "error", "Không tìm thấy bộ thẻ!");
            }

            Deck existingDeck = existingDeckOpt.get();
            deck.setUser(existingDeck.getUser());
            deck.setCreatedAt(existingDeck.getCreatedAt());
            deck.setId(id);

            deckService.saveDeck(deck);
            return redirectWithMessage("/decks", redirectAttributes, "success", "Cập nhật thành công!");
        } catch (Exception e) {
            return redirectWithMessage("/decks", redirectAttributes, "error", "Có lỗi xảy ra: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteDeck(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            if (deckService.getDeckById(id).isPresent()) {
                deckService.deleteDeck(id);
                return redirectWithMessage("/decks", redirectAttributes, "success", "Bộ thẻ đã được xóa thành công!");
            } else {
                return redirectWithMessage("/decks", redirectAttributes, "error", "Không tìm thấy bộ thẻ!");
            }
        } catch (Exception e) {
            return redirectWithMessage("/decks", redirectAttributes, "error",
                    "Có lỗi xảy ra khi xóa: " + e.getMessage());
        }
    }
}
