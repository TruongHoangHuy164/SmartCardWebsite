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

import com.quizletclone.flashcard.config.OnlineUserSessionListener;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/signup")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        // Truyền thông báo nếu có
        if (model.containsAttribute("success")) {
            model.addAttribute("success", model.getAttribute("success"));
        }
        if (model.containsAttribute("error")) {
            model.addAttribute("error", model.getAttribute("error"));
        }
        return "login/signup";
    }

    @PostMapping("/signup")
    public String register(@ModelAttribute User user, @RequestParam(required = false) String confirmPassword,
            Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            if (userService.findByUsername(user.getUsername()).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Tên đăng nhập đã tồn tại!");
                return "redirect:/signup";
            }

            if (userService.findByEmail(user.getEmail()).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Email đã tồn tại!");
                return "redirect:/signup";
            }

            // Kiểm tra xác nhận mật khẩu
            if (confirmPassword == null || !user.getPassword().equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp!");
                return "redirect:/signup";
            }

            userService.saveUserWithRole(user, "USER");
            // lưu created_at
            user.setCreatedAt(new java.util.Date());
            userService.saveUser(user);
            session.setAttribute("loggedInUser", user);
            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công!");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi đăng ký: " + e.getMessage());
            return "redirect:/signup";
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

                // Check if user is enabled before checking password
                if (user.getEnabled() == null || !user.getEnabled()) {
                    model.addAttribute("error", "Tài khoản này đã bị khóa!");
                    return "login/login";
                }

                if (user.getPassword().equals(password)) {
                    // Đăng nhập đúng: reset số lần sai
                    user.setFailedLoginAttempts(0);
                    userService.saveUser(user);
                    session.setAttribute("loggedInUser", user); // Lưu session
                    // Đánh dấu user online
                    OnlineUserSessionListener.addOnlineUser(user.getId());
                    if (user.getRole() != null && "ADMIN".equals(user.getRole().getName())) {
                        return redirectWithMessage("/admin", redirectAttributes, "success",
                                "Đăng nhập admin thành công!");
                    } else {
                        return redirectWithMessage("/", redirectAttributes, "success", "Đăng nhập thành công!");
                    }
                } else {
                    // Đăng nhập sai: tăng số lần sai
                    int failed = user.getFailedLoginAttempts() == null ? 0 : user.getFailedLoginAttempts();
                    failed++;
                    user.setFailedLoginAttempts(failed);
                    // Nếu sai >= 3 lần thì khóa tài khoản
                    if (failed >= 3) {
                        user.setEnabled(false);
                        userService.saveUser(user);
                        model.addAttribute("error", "Bạn đã nhập sai 3 lần. Tài khoản đã bị khóa!");
                        return "login/login";
                    } else {
                        userService.saveUser(user);
                        model.addAttribute("error", "Mật khẩu không đúng! Bạn còn " + (3 - failed) + " lần thử.");
                    }
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
        Object userObj = session.getAttribute("loggedInUser");
        if (userObj instanceof com.quizletclone.flashcard.model.User user) {
            OnlineUserSessionListener.removeOnlineUser(user.getId());
        }
        session.invalidate(); // Xóa session
        return redirectWithMessage("/decks", redirectAttributes, "success", "Đăng xuất thành công!");
    }

    @GetMapping("/account-locked")
    public String accountLocked() {
        return "error/account-locked";
    }
}
