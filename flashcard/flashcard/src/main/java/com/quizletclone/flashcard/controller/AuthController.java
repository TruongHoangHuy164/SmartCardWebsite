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

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
        try {
            if (userService.findByUsername(user.getUsername()).isPresent()) {
                model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
                return "auth/register";
            }

            if (userService.findByEmail(user.getEmail()).isPresent()) {
                model.addAttribute("error", "Email đã tồn tại!");
                return "auth/register";
            }

            userService.saveUser(user);
            return "redirect:/login?success=Đăng ký thành công! Vui lòng đăng nhập.";
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi đăng ký: " + e.getMessage());
            return "auth/register";
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
        return "auth/login";
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
                    return redirectWithMessage("/decks", redirectAttributes, "success", "Đăng nhập thành công!");
                } else {
                    model.addAttribute("error", "Mật khẩu không đúng!");
                }
            } else {
                model.addAttribute("error", "Tên đăng nhập không tồn tại!");
            }

            return "auth/login";

        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi đăng nhập: " + e.getMessage());
            return "auth/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate(); // Xóa session
        return redirectWithMessage("/decks", redirectAttributes, "success", "Đăng xuất thành công!");
    }
}
