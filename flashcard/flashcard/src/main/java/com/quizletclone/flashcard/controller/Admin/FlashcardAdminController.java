package com.quizletclone.flashcard.controller.Admin;

import com.quizletclone.flashcard.service.FlashcardService;
import com.quizletclone.flashcard.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.quizletclone.flashcard.model.Flashcard;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.quizletclone.flashcard.util.ImageHelper;
import java.io.IOException;

@Controller
public class FlashcardAdminController {
    @Autowired
    private FlashcardService flashcardService;
    @Autowired
    private AIService aiService;

    @GetMapping("/admin/flashcards")
    public String manageFlashcards(Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "error/404";
        }
        model.addAttribute("flashcards", flashcardService.getAllFlashcards());
        return "admin/flashcards";
    }

    @GetMapping("/admin/flashcards/add")
    public String showAddFlashcardForm(@RequestParam("deckId") Integer deckId, Model model, HttpSession session) {
        if (!isAdmin(session)) return "error/404";
        Flashcard flashcard = new Flashcard();
        com.quizletclone.flashcard.model.Deck deck = new com.quizletclone.flashcard.model.Deck();
        deck.setId(deckId);
        flashcard.setDeck(deck);
        model.addAttribute("flashcard", flashcard);
        model.addAttribute("deckId", deckId);
        return "admin/decks/flashcards/add";
    }

    @PostMapping("/admin/flashcards/add")
    public String addFlashcard(@ModelAttribute("flashcard") Flashcard flashcard, 
                             @RequestParam("imageFile") MultipartFile imageFile,
                             Model model, HttpSession session) {
        if (!isAdmin(session)) return "error/404";
        Integer deckId = flashcard.getDeck().getId();
        // Text moderation
        if (aiService.isSensitiveText(flashcard.getTerm()) || aiService.isSensitiveText(flashcard.getDefinition())) {
            model.addAttribute("flashcard", flashcard);
            model.addAttribute("error", "Nội dung chứa từ ngữ nhạy cảm!");
            model.addAttribute("deckId", deckId);
            return "admin/decks/flashcards/add";
        }
        // Image moderation before saving
        if (!imageFile.isEmpty()) {
            if (aiService.isSensitiveImage(imageFile)) {
                model.addAttribute("flashcard", flashcard);
                model.addAttribute("error", "Ảnh có nội dung nhạy cảm!");
                model.addAttribute("deckId", deckId);
                return "admin/decks/flashcards/add";
            }
            try {
                String imageUrl = ImageHelper.saveImage(imageFile, "upload");
                flashcard.setImageUrl(imageUrl);
            } catch (IOException e) {
                model.addAttribute("flashcard", flashcard);
                model.addAttribute("error", "Lỗi khi tải lên hình ảnh!");
                model.addAttribute("deckId", deckId);
                return "admin/decks/flashcards/add";
            }
        }
        flashcardService.saveFlashcard(flashcard);
        return "redirect:/admin/decks/view/" + deckId;
    }

    @GetMapping("/admin/flashcards/edit/{id}")
    public String showEditFlashcardForm(@PathVariable Integer id, Model model, HttpSession session) {
        if (!isAdmin(session)) return "error/404";
        var flashcardOpt = flashcardService.getFlashcardById(id);
        if (flashcardOpt.isEmpty()) return "error/404";
        Flashcard flashcard = flashcardOpt.get();
        model.addAttribute("flashcard", flashcard);
        model.addAttribute("deckId", flashcard.getDeck().getId());
        return "admin/decks/flashcards/edit";
    }

    @PostMapping("/admin/flashcards/edit/{id}")
    public String editFlashcard(@PathVariable Integer id, 
                                @ModelAttribute("flashcard") Flashcard flashcard, 
                                @RequestParam("imageFile") MultipartFile imageFile,
                                Model model, HttpSession session) {
        if (!isAdmin(session)) return "error/404";
        Integer deckId = flashcard.getDeck().getId();
        // Text moderation
        if (aiService.isSensitiveText(flashcard.getTerm()) || aiService.isSensitiveText(flashcard.getDefinition())) {
            model.addAttribute("flashcard", flashcard);
            model.addAttribute("error", "Nội dung chứa từ ngữ nhạy cảm!");
            model.addAttribute("deckId", deckId);
            return "admin/decks/flashcards/edit";
        }
        // Image moderation before saving
        if (!imageFile.isEmpty()) {
            if (aiService.isSensitiveImage(imageFile)) {
                model.addAttribute("flashcard", flashcard);
                model.addAttribute("error", "Ảnh có nội dung nhạy cảm!");
                model.addAttribute("deckId", deckId);
                return "admin/decks/flashcards/edit";
            }
            try {
                String imageUrl = ImageHelper.saveImage(imageFile, "upload");
                flashcard.setImageUrl(imageUrl);
            } catch (IOException e) {
                model.addAttribute("flashcard", flashcard);
                model.addAttribute("error", "Lỗi khi tải lên hình ảnh!");
                model.addAttribute("deckId", deckId);
                return "admin/decks/flashcards/edit";
            }
        }
        flashcardService.saveFlashcard(flashcard);
        return "redirect:/admin/decks/view/" + deckId;
    }

    @GetMapping("/admin/flashcards/delete/{id}")
    public String showDeleteConfirmation(@PathVariable Integer id, Model model, HttpSession session) {
        if (!isAdmin(session)) return "error/404";
        var flashcardOpt = flashcardService.getFlashcardById(id);
        if (flashcardOpt.isEmpty()) return "error/404";
        Flashcard flashcard = flashcardOpt.get();
        model.addAttribute("flashcard", flashcard);
        model.addAttribute("deckId", flashcard.getDeck().getId());
        return "admin/decks/flashcards/delete";
    }
    
    @PostMapping("/admin/flashcards/delete/{id}")
    public String deleteFlashcard(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) return "error/404";
        var flashcardOpt = flashcardService.getFlashcardById(id);
        if (flashcardOpt.isEmpty()) return "error/404";
        Integer deckId = flashcardOpt.get().getDeck().getId();
        flashcardService.deleteFlashcard(id);
        return "redirect:/admin/decks/view/" + deckId;
    }

    private boolean isAdmin(HttpSession session) {
        Object userObj = session.getAttribute("loggedInUser");
        if (userObj instanceof com.quizletclone.flashcard.model.User user) {
            return user.getRole() != null && "ADMIN".equals(user.getRole().getName());
        }
        return false;
    }
} 