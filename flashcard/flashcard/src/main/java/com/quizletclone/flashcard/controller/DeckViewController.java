package com.quizletclone.flashcard.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

    // ========================
    // GET: Hiển thị tất cả bộ thẻ
    // ========================
    @GetMapping
    public String listDecks(Model model,
            HttpSession session,
            @RequestParam(defaultValue = "0") int pageMy,
            @RequestParam(defaultValue = "0") int pageOther,
            @RequestParam(defaultValue = "8") int size) {
        try {
            User user = getLoggedInUser(session);
            List<Deck> myDecks = user != null ? deckService.getDecksByUser(user) : new ArrayList<>();
            List<Deck> otherDecks = user != null ? deckService.getDecksNotByUser(user) : deckService.getAllDecks();

            // Phân trang bộ thẻ của tôi
            int totalMyPages = (int) Math.ceil((double) myDecks.size() / size);
            int fromMy = Math.min(pageMy * size, myDecks.size());
            int toMy = Math.min(fromMy + size, myDecks.size());
            List<Deck> pagedMyDecks = myDecks.subList(fromMy, toMy);

            // Phân trang bộ thẻ của người khác
            int totalOtherPages = (int) Math.ceil((double) otherDecks.size() / size);
            int fromOther = Math.min(pageOther * size, otherDecks.size());
            int toOther = Math.min(fromOther + size, otherDecks.size());
            List<Deck> pagedOtherDecks = otherDecks.subList(fromOther, toOther);

            // Tạo map thống kê cho từng deck được hiển thị
            Map<Integer, Integer> totalQuestionsMap = new HashMap<>();
            Map<Integer, Integer> recentCorrectCountMap = new HashMap<>();
            Map<Integer, Integer> needReviewMap = new HashMap<>();

            for (Deck deck : pagedMyDecks) {
                int total = quizQuestionService.countByDeckId(deck.getId());
                int correct = user != null ? quizService.getCorrectAnswersFromLatestQuiz(user.getId(), deck.getId())
                        : 0;
                totalQuestionsMap.put(deck.getId(), total);
                recentCorrectCountMap.put(deck.getId(), correct);
                needReviewMap.put(deck.getId(), total - correct);
            }

            for (Deck deck : pagedOtherDecks) {
                int total = quizQuestionService.countByDeckId(deck.getId());
                int correct = user != null ? quizService.getCorrectAnswersFromLatestQuiz(user.getId(), deck.getId())
                        : 0;
                totalQuestionsMap.put(deck.getId(), total);
                recentCorrectCountMap.put(deck.getId(), correct);
                needReviewMap.put(deck.getId(), total - correct);
            }

            model.addAttribute("pagedMyDecks", pagedMyDecks);
            model.addAttribute("pagedOtherDecks", pagedOtherDecks);

            model.addAttribute("pageMy", pageMy);
            model.addAttribute("totalMyPages", totalMyPages);
            model.addAttribute("pageOther", pageOther);
            model.addAttribute("totalOtherPages", totalOtherPages);

            model.addAttribute("totalQuestionsMap", totalQuestionsMap);
            model.addAttribute("recentCorrectCountMap", recentCorrectCountMap);
            model.addAttribute("needReviewMap", needReviewMap);

            return "deck/list";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách bộ thẻ: " + e.getMessage());
            return "deck/list";
        }
    }

    // ========================
    // GET: Tìm kiếm bộ thẻ
    // ========================
    @GetMapping("/search")
    public String searchDecks(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String sub,
            Model model, HttpSession session,
            @RequestParam(defaultValue = "0") int pageMy,
            @RequestParam(defaultValue = "0") int pageOther,
            @RequestParam(defaultValue = "8") int size) {
        try {
            User user = getLoggedInUser(session);
            List<Deck> myDecks = deckService.searchAndSortDecksByUser(user, true, keyword, sort, sub);
            List<Deck> otherDecks = deckService.searchAndSortDecksByUser(user, false, keyword, sort, sub);

            // Phân trang bộ thẻ của tôi
            int totalMyPages = (int) Math.ceil((double) myDecks.size() / size);
            int fromMy = Math.min(pageMy * size, myDecks.size());
            int toMy = Math.min(fromMy + size, myDecks.size());
            List<Deck> subMyDecks = myDecks.subList(fromMy, toMy);
            Page<Deck> pagedMyDecks = new PageImpl<>(subMyDecks);

            // Phân trang bộ thẻ của người khác
            int totalOtherPages = (int) Math.ceil((double) otherDecks.size() / size);
            int fromOther = Math.min(pageOther * size, otherDecks.size());
            int toOther = Math.min(fromOther + size, otherDecks.size());
            List<Deck> subOtherDecks = otherDecks.subList(fromOther, toOther);
            Page<Deck> pagedOtherDecks = new PageImpl<>(subOtherDecks);

            // Tạo map thống kê cho từng deck được hiển thị
            Map<Integer, Integer> totalQuestionsMap = new HashMap<>();
            Map<Integer, Integer> recentCorrectCountMap = new HashMap<>();
            Map<Integer, Integer> needReviewMap = new HashMap<>();

            for (Deck deck : pagedMyDecks) {
                int total = quizQuestionService.countByDeckId(deck.getId());
                int correct = user != null ? quizService.getCorrectAnswersFromLatestQuiz(user.getId(), deck.getId())
                        : 0;
                totalQuestionsMap.put(deck.getId(), total);
                recentCorrectCountMap.put(deck.getId(), correct);
                needReviewMap.put(deck.getId(), total - correct);
            }

            for (Deck deck : pagedOtherDecks) {
                int total = quizQuestionService.countByDeckId(deck.getId());
                int correct = user != null ? quizService.getCorrectAnswersFromLatestQuiz(user.getId(), deck.getId())
                        : 0;
                totalQuestionsMap.put(deck.getId(), total);
                recentCorrectCountMap.put(deck.getId(), correct);
                needReviewMap.put(deck.getId(), total - correct);
            }

            model.addAttribute("pagedMyDecks", pagedMyDecks);
            model.addAttribute("pagedOtherDecks", pagedOtherDecks);

            model.addAttribute("pageMy", pageMy);
            model.addAttribute("totalMyPages", totalMyPages);
            model.addAttribute("pageOther", pageOther);
            model.addAttribute("totalOtherPages", totalOtherPages);

            model.addAttribute("totalQuestionsMap", totalQuestionsMap);
            model.addAttribute("recentCorrectCountMap", recentCorrectCountMap);
            model.addAttribute("needReviewMap", needReviewMap);

            return "deck/list :: deckCard";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách bộ thẻ: " + e.getMessage());
            return "deck/list";
        }
    }

    // ========================
    // GET: Hiển thị form tạo
    // ========================
    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (getLoggedInUser(session) == null) {
            return redirectWithMessage("/login", redirectAttributes, "error", "Vui lòng đăng nhập để tạo bộ thẻ.");
        }
        model.addAttribute("deck", new Deck());
        return "deck/add";
    }

    // ========================
    // POST: Xử lý tạo mới
    // ========================
    @PostMapping("/create")
    public String createDeck(@ModelAttribute Deck deck,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            User loggedInUser = getLoggedInUser(session);
            if (loggedInUser == null) {
                return redirectWithMessage("/login", redirectAttributes, "error", "Vui lòng đăng nhập để tạo bộ thẻ.");
            }

            // giới hạn tên deck không quá 30 kí tự
            if (deck.getTitle().length() > 30) {
                return redirectWithMessage("/decks/create", redirectAttributes, "error",
                        "Tiêu đề bộ thẻ không được vượt quá 30 ký tự.");
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

    // ========================
    // GET: Xem chi tiết bộ thẻ
    // ========================
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

    // ========================
    // GET: Form chỉnh sửa
    // ========================
    @GetMapping("/{id}/edit")
    public String editDeck(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Deck> deckOpt = deckService.getDeckById(id);
        if (deckOpt.isPresent()) {
            model.addAttribute("deck", deckOpt.get());
            return "deck/edit";
        } else {
            return redirectWithMessage("/decks", redirectAttributes, "error", "Không tìm thấy bộ thẻ!");
        }
    }

    // ========================
    // POST: Cập nhật
    // ========================
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
            deck.setId(existingDeck.getId());
            deck.setUser(existingDeck.getUser());
            deck.setCreatedAt(existingDeck.getCreatedAt());

            deckService.saveDeck(deck);
            return redirectWithMessage("/decks", redirectAttributes, "success", "Cập nhật thành công!");
        } catch (Exception e) {
            return redirectWithMessage("/decks", redirectAttributes, "error", "Có lỗi xảy ra: " + e.getMessage());
        }
    }

    // ========================
    // GET: Xoá bộ thẻ
    // ========================
    @GetMapping("/{id}/delete")
    public String deleteDeck(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Deck> deckOpt = deckService.getDeckById(id);
            if (deckOpt.isPresent()) {
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

    // ========================
    // PRIVATE: Lấy user từ session
    // ========================
    private User getLoggedInUser(HttpSession session) {
        return (User) session.getAttribute("loggedInUser");
    }
}
