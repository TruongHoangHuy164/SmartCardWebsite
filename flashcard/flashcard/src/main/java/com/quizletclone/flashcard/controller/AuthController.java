package com.quizletclone.flashcard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.quizletclone.flashcard.model.User;
import com.quizletclone.flashcard.service.UserService;
import static com.quizletclone.flashcard.util.UrlHelper.redirectWithMessage;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/signup")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "login/signup";
    }

    @PostMapping("/signup")
    public String register(@ModelAttribute User user, Model model, HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            if (userService.findByUsername(user.getUsername()).isPresent()) {
                model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
                return "login/signup";
            }

            if (userService.findByEmail(user.getEmail()).isPresent()) {
                model.addAttribute("error", "Email đã tồn tại!");
                return "login/signup";
            }

            userService.saveUser(user);
            // Tự động đăng nhập sau khi đăng ký
            session.setAttribute("loggedInUser", user);
            return redirectWithMessage("/", redirectAttributes, "success", "Đăng ký và đăng nhập thành công!");
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi đăng ký: " + e.getMessage());
            return "login/signup";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(required = false) String success,
            @RequestParam(required = false) String error,
            Model model) {
        if (success != null) {
            model.addAttribute("success", success);
        }
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "login/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
            @RequestParam String password,
            Model model,
            HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            var userOpt = userService.findByUsername(username);

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                if (user.getPassword().equals(password)) {
                    session.setAttribute("loggedInUser", user); // Lưu session
                    return redirectWithMessage("/", redirectAttributes, "success", "Đăng nhập thành công!");
                } else {
                    model.addAttribute("error", "Mật khẩu không đúng!");
                }
            } else {
                model.addAttribute("error", "Tên đăng nhập không tồn tại!");
            }

            return "login/login";

        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi đăng nhập: " + e.getMessage());
            return "login/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate(); // Xóa session
        return redirectWithMessage("/decks", redirectAttributes, "success", "Đăng xuất thành công!");
    }
}