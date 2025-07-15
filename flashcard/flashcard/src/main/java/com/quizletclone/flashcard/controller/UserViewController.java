package com.quizletclone.flashcard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.quizletclone.flashcard.model.User;
import com.quizletclone.flashcard.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/users")
public class UserViewController {
    @Autowired
    private UserService userService;

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "user/list";
    }

    @GetMapping("/detail/{id}")
    public String userDetail(@PathVariable Integer id, Model model) {
        userService.findById(id).ifPresent(user -> model.addAttribute("user", user));
        return "user/detail";
    }

    @GetMapping("/form")
    public String userForm(@RequestParam(value = "id", required = false) Integer id, Model model) {
        if (id != null) {
            userService.findById(id).ifPresent(user -> model.addAttribute("user", user));
        } else {
            model.addAttribute("user", new com.quizletclone.flashcard.model.User());
        }
        return "user/form";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") com.quizletclone.flashcard.model.User user,
            RedirectAttributes redirectAttributes) {
        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("toast", "Lưu người dùng thành công!");
        return "redirect:/users";
    }

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        try {
            // Lấy user từ session
            User loggedInUser = (User) session.getAttribute("loggedInUser");

            if (loggedInUser == null) {
                // Nếu chưa đăng nhập, chuyển hướng đến trang đăng nhập
                return "redirect:/login?error=Vui lòng đăng nhập để xem hồ sơ.";
            }

            // Optional: Lấy lại user từ DB để đảm bảo dữ liệu mới nhất (nếu cần)
            var userOpt = userService.findById(loggedInUser.getId());
            if (userOpt.isPresent()) {
                model.addAttribute("user", userOpt.get());
            } else {
                model.addAttribute("user", loggedInUser); // fallback từ session
            }

            return "user/profile";

        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "user/profile";
        }
    }
}