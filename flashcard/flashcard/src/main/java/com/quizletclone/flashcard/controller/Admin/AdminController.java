package com.quizletclone.flashcard.controller.Admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import com.quizletclone.flashcard.service.DeckService;
import com.quizletclone.flashcard.service.FlashcardService;

@Controller
public class AdminController {
    @Autowired
    private DeckService deckService;
    @Autowired
    private FlashcardService flashcardService;

    @GetMapping("/admin")
    public String adminDashboard(Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "error/404";
        }
        // Có thể truyền thêm thông tin nếu cần
        return "admin/layout";
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(@RequestParam(value = "month", required = false) Integer month,
                           @RequestParam(value = "year", required = false) Integer year,
                           Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "error/404";
        }
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<Integer> years = new ArrayList<>();
        for (int y = currentYear; y >= currentYear - 5; y--) years.add(y);
        model.addAttribute("years", years);
        int deckCount = (month != null && year != null)
            ? deckService.countDecksByMonthAndYear(month, year)
            : deckService.getAllDecks().size();
        int flashcardCount = (month != null && year != null)
            ? flashcardService.countFlashcardsByMonthAndYear(month, year)
            : flashcardService.getAllFlashcards().size();
        int onlineUsers = com.quizletclone.flashcard.config.OnlineUserSessionListener.ONLINE_USER_IDS.size();
        model.addAttribute("deckCount", deckCount);
        model.addAttribute("flashcardCount", flashcardCount);
        model.addAttribute("onlineUsers", onlineUsers);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedYear", year);
        return "admin/dashboard";
    }
    // Đã chuyển các route quản lý user, deck, flashcard sang controller riêng
    private boolean isAdmin(HttpSession session) {
        Object userObj = session.getAttribute("loggedInUser");
        if (userObj instanceof com.quizletclone.flashcard.model.User user) {
            return user.getRole() != null && "ADMIN".equals(user.getRole().getName());
        }
        return false;
    }
}