package com.quizletclone.flashcard.controller.Admin;

import com.quizletclone.flashcard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PostMapping("/admin/users/toggle-lock")
    public String toggleUserLock(@RequestParam("userId") Integer userId) {
        userService.findById(userId).ifPresent(user -> {
            boolean currentStatus = user.getEnabled() != null && user.getEnabled();
            userService.setUserEnabled(userId, !currentStatus);
        });
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/change-role")
    public String changeUserRole(@RequestParam("userId") Integer userId, @RequestParam("roleName") String roleName) {
        userService.changeUserRole(userId, roleName);
        return "redirect:/admin/users";
    }

    private boolean isAdmin(HttpSession session) {
        Object userObj = session.getAttribute("loggedInUser");
        if (userObj instanceof com.quizletclone.flashcard.model.User user) {
            return user.getRole() != null && "ADMIN".equals(user.getRole().getName());
        }
        return false;
    }
} 