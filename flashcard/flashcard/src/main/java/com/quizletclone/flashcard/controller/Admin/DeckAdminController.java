package com.quizletclone.flashcard.controller.Admin;

import com.quizletclone.flashcard.service.DeckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class DeckAdminController {
    @Autowired
    private DeckService deckService;

    @GetMapping("/admin/decks")
    public String manageDecks(Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "error/404";
        }
        model.addAttribute("decks", deckService.getAllDecks());
        return "admin/decks";
    }

    private boolean isAdmin(HttpSession session) {
        Object userObj = session.getAttribute("loggedInUser");
        if (userObj instanceof com.quizletclone.flashcard.model.User user) {
            return user.getRole() != null && "ADMIN".equals(user.getRole().getName());
        }
        return false;
    }
} 