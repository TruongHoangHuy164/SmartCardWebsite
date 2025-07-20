package com.quizletclone.flashcard.controller.Admin;

import com.quizletclone.flashcard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserAdminController {
    @Autowired
    private UserService userService;

    @GetMapping("/admin/users")
    public String manageUsers(Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "error/404";
        }
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    private boolean isAdmin(HttpSession session) {
        Object userObj = session.getAttribute("loggedInUser");
        if (userObj instanceof com.quizletclone.flashcard.model.User user) {
            return user.getRole() != null && "ADMIN".equals(user.getRole().getName());
        }
        return false;
    }
} 