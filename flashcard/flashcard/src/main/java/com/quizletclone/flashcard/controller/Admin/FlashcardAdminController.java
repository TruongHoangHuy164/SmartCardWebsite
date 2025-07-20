package com.quizletclone.flashcard.controller.Admin;

import com.quizletclone.flashcard.service.FlashcardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class FlashcardAdminController {
    @Autowired
    private FlashcardService flashcardService;

    @GetMapping("/admin/flashcards")
    public String manageFlashcards(Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "error/404";
        }
        model.addAttribute("flashcards", flashcardService.getAllFlashcards());
        return "admin/flashcards";
    }

    private boolean isAdmin(HttpSession session) {
        Object userObj = session.getAttribute("loggedInUser");
        if (userObj instanceof com.quizletclone.flashcard.model.User user) {
            return user.getRole() != null && "ADMIN".equals(user.getRole().getName());
        }
        return false;
    }
} 