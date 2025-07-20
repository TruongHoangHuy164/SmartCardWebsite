package com.quizletclone.flashcard.controller.Admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {
    @GetMapping("/admin")
    public String adminDashboard(Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "error/404";
        }
        // Có thể truyền thêm thông tin nếu cần
        return "admin/layout";
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