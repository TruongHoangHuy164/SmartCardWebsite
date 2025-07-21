package com.quizletclone.flashcard.controller.Admin;

import com.quizletclone.flashcard.service.DeckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.quizletclone.flashcard.service.AIService;

@Controller
public class DeckAdminController {
    @Autowired
    private DeckService deckService;

    @Autowired
    private AIService aiService;

    @GetMapping("/admin/decks")
    public String manageDecks(Model model, HttpSession session,
                             @RequestParam(value = "keyword", required = false) String keyword,
                             @RequestParam(value = "subject", required = false) String subject,
                             @RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "size", defaultValue = "10") int size) {
        if (!isAdmin(session)) {
            return "error/404";
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<com.quizletclone.flashcard.model.Deck> deckPage = deckService.getDecks(keyword, subject, pageable);
        model.addAttribute("deckPage", deckPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("subject", subject);
        return "admin/decks";
    }

    @GetMapping("/admin/decks/add")
    public String showAddDeckForm(Model model, HttpSession session) {
        if (!isAdmin(session)) return "error/404";
        model.addAttribute("deck", new com.quizletclone.flashcard.model.Deck());
        return "admin/decks/add";
    }

    @PostMapping("/admin/decks/add")
    public String addDeck(@ModelAttribute("deck") com.quizletclone.flashcard.model.Deck deck, HttpSession session, Model model) {
        if (!isAdmin(session)) return "error/404";
        // Kiểm duyệt nội dung
        if (aiService.isSensitiveText(deck.getTitle()) || aiService.isSensitiveText(deck.getDescription())) {
            model.addAttribute("deck", deck);
            model.addAttribute("error", "Tên hoặc mô tả bộ thẻ chứa nội dung không phù hợp!");
            return "admin/decks/add";
        }
       
        Object userObj = session.getAttribute("loggedInUser");
        if (userObj instanceof com.quizletclone.flashcard.model.User user) {
            deck.setUser(user);
        }
        deck.setCreatedAt(new java.util.Date());
        deckService.saveDeck(deck);
        return "redirect:/admin/decks";
    }

    @GetMapping("/admin/decks/edit/{id}")
    public String showEditDeckForm(@PathVariable Integer id, Model model, HttpSession session) {
        if (!isAdmin(session)) return "error/404";
        var deckOpt = deckService.getDeckById(id);
        if (deckOpt.isEmpty()) return "error/404";
        model.addAttribute("deck", deckOpt.get());
        return "admin/decks/edit";
    }

    @PostMapping("/admin/decks/edit/{id}")
    public String editDeck(@PathVariable Integer id, @ModelAttribute("deck") com.quizletclone.flashcard.model.Deck deck, HttpSession session, Model model) {
        if (!isAdmin(session)) return "error/404";
        // Kiểm duyệt nội dung
        if (aiService.isSensitiveText(deck.getTitle()) || aiService.isSensitiveText(deck.getDescription())) {
            model.addAttribute("deck", deck);
            model.addAttribute("error", "Tên hoặc mô tả bộ thẻ chứa nội dung không phù hợp!");
            return "admin/decks/edit";
        }
      
        var oldDeckOpt = deckService.getDeckById(id);
        if (oldDeckOpt.isEmpty()) return "error/404";
        var oldDeck = oldDeckOpt.get();
        oldDeck.setTitle(deck.getTitle());
        oldDeck.setDescription(deck.getDescription());
        oldDeck.setSubject(deck.getSubject());
        oldDeck.setIsPublic(deck.getIsPublic());
      
        deckService.saveDeck(oldDeck);
        return "redirect:/admin/decks";
    }

    @GetMapping("/admin/decks/delete/{id}")
    public String deleteDeck(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) return "error/404";
        deckService.deleteDeck(id);
        return "redirect:/admin/decks";
    }

    @GetMapping("/admin/decks/view/{id}")
    public String viewDeck(@PathVariable Integer id, Model model, HttpSession session) {
        if (!isAdmin(session)) return "error/404";
        var deckOpt = deckService.getDeckById(id);
        if (deckOpt.isEmpty()) return "error/404";
        var deck = deckOpt.get();
        model.addAttribute("deck", deck);
        model.addAttribute("flashcards", deck.getFlashcards());
        return "admin/decks/view";
    }

    private boolean isAdmin(HttpSession session) {
        Object userObj = session.getAttribute("loggedInUser");
        if (userObj instanceof com.quizletclone.flashcard.model.User user) {
            return user.getRole() != null && "ADMIN".equals(user.getRole().getName());
        }
        return false;
    }
} 