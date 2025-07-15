package com.quizletclone.flashcard.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.quizletclone.flashcard.model.Deck;
import com.quizletclone.flashcard.model.User;
import com.quizletclone.flashcard.service.DeckService;
import com.quizletclone.flashcard.service.UserService;
import static com.quizletclone.flashcard.util.UrlHelper.redirectWithMessage;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/decks")
@RequiredArgsConstructor
public class DeckViewController {
    private final DeckService deckService;
    private final UserService userService;

    @GetMapping
    public String listDecks(Model model) {
        try {
            List<Deck> decks = deckService.getAllDecks();
            model.addAttribute("decks", decks);
            return "deck/list";
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi tải danh sách bộ thẻ: " + e.getMessage());
            return "deck/list";
        }
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("deck", new Deck());
        return "deck/add";
    }

    @PostMapping("/create")
    public String createDeck(@ModelAttribute Deck deck, RedirectAttributes redirectAttributes) {
        try {
            Optional<User> userOpt = userService.findById(1); // Tạm thời dùng user cố định
            if (userOpt.isPresent()) {
                deck.setUser(userOpt.get());
                deck.setIsPublic(true);
                deckService.saveDeck(deck);
                return redirectWithMessage("/decks", redirectAttributes, "success", "Bộ thẻ đã được tạo thành công!");
            } else {
                return redirectWithMessage("/decks", redirectAttributes, "error", "Không tìm thấy người dùng!");
            }
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
