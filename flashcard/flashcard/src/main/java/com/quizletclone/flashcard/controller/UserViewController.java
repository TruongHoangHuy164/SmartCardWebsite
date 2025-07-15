package com.quizletclone.flashcard.controller;

import com.quizletclone.flashcard.model.User;
import com.quizletclone.flashcard.service.UserService;
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

import java.util.List;

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
        userService.getUserById(id).ifPresent(user -> model.addAttribute("user", user));
        return "user/detail";
    }

    @GetMapping("/form")
    public String userForm(@RequestParam(value = "id", required = false) Integer id, Model model) {
        if (id != null) {
            userService.getUserById(id).ifPresent(user -> model.addAttribute("user", user));
        } else {
            model.addAttribute("user", new com.quizletclone.flashcard.model.User());
        }
        return "user/form";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") com.quizletclone.flashcard.model.User user, RedirectAttributes redirectAttributes) {
        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("toast", "Lưu người dùng thành công!");
        return "redirect:/users";
    }
} 