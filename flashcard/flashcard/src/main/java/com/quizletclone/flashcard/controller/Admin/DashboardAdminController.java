package com.quizletclone.flashcard.controller.Admin;

import com.quizletclone.flashcard.config.OnlineUserSessionListener;
import com.quizletclone.flashcard.model.Deck;
import com.quizletclone.flashcard.model.Flashcard;
import com.quizletclone.flashcard.service.DeckService;
import com.quizletclone.flashcard.service.FlashcardService;
import com.quizletclone.flashcard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardAdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private DeckService deckService;
    @Autowired
    private FlashcardService flashcardService;

    // TODO: Đếm user online thực tế bằng session listener
    // Gợi ý: Tạo 1 HttpSessionListener, khi user login thì thêm userId vào Set, khi logout/session destroy thì xóa userId khỏi Set
    // Ở đây chỉ demo, bạn cần cài đặt listener thực tế để cập nhật onlineUserIds

    @GetMapping("/stats")
    public Map<String, Object> getDashboardStats(
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year) {
        Map<String, Object> result = new HashMap<>();

        // Số user online thực tế
        result.put("onlineUsers", OnlineUserSessionListener.ONLINE_USER_IDS.size());

        // Thống kê deck
        int deckCount;
        if (month != null && year != null) {
            deckCount = deckService.countDecksByMonthAndYear(month, year);
        } else {
            deckCount = deckService.getAllDecks().size();
        }
        result.put("deckCount", deckCount);

        // Thống kê flashcard
        int flashcardCount;
        if (month != null && year != null) {
            flashcardCount = flashcardService.countFlashcardsByMonthAndYear(month, year);
        } else {
            flashcardCount = flashcardService.getAllFlashcards().size();
        }
        result.put("flashcardCount", flashcardCount);

        return result;
    }
} 