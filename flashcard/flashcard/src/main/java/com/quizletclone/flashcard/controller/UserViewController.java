package com.quizletclone.flashcard.controller;

import java.util.Date;
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
import com.quizletclone.flashcard.util.ImageHelper;

import com.quizletclone.flashcard.model.Deck;
import com.quizletclone.flashcard.model.Quiz;
import com.quizletclone.flashcard.model.User;
import com.quizletclone.flashcard.repository.DeckRepository;
import com.quizletclone.flashcard.repository.QuizRepository;
import com.quizletclone.flashcard.service.FlashcardService;
import com.quizletclone.flashcard.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/users")
public class UserViewController {
    // Giao diện chỉnh sửa hồ sơ
    @GetMapping("/edit-profile")
    public String editProfileForm(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        User user = userService.findById(loggedInUser.getId()).orElse(loggedInUser);
        model.addAttribute("user", user);
        return "user/edit_profile";
    }

    @PostMapping("/edit-profile")
    public String editProfileSubmit(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam(required = false) String avatarUrl,
            @RequestParam(value = "avatarFile", required = false) org.springframework.web.multipart.MultipartFile avatarFile,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        User user = userService.findById(loggedInUser.getId()).orElse(loggedInUser);
        user.setUsername(username);
        user.setEmail(email);
        try {
            if (avatarFile != null && !avatarFile.isEmpty()) {
                // Lưu file upload vào thư mục uploads/avatar
                String savedPath = ImageHelper.saveImage(avatarFile, "avatar");
                user.setAvatarUrl(savedPath);
            } else if (avatarUrl != null && !avatarUrl.isBlank()) {
                // Nếu nhập URL, tải về và lưu vào uploads/avatar
                String savedPath = ImageHelper.downloadImageFromUrl(avatarUrl, "avatar");
                user.setAvatarUrl(savedPath);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi upload hoặc tải ảnh đại diện!");
            return "redirect:/users/edit-profile";
        }
        userService.saveUser(user);
        session.setAttribute("loggedInUser", user); // update session
        redirectAttributes.addFlashAttribute("toast", "Cập nhật hồ sơ thành công!");
        return "redirect:/users/profile";
    }

    // Giao diện đổi mật khẩu
    @GetMapping("/change-password")
    public String changePasswordForm(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        User user = userService.findById(loggedInUser.getId()).orElse(loggedInUser);
        model.addAttribute("user", user);
        return "user/change_password";
    }

    @PostMapping("/change-password")
    public String changePasswordSubmit(@RequestParam String currentPassword, @RequestParam String newPassword, @RequestParam String confirmNewPassword, @RequestParam String otp, @RequestParam String email, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        User user = userService.findById(loggedInUser.getId()).orElse(loggedInUser);
        if (!user.getEmail().equals(email)) {
            redirectAttributes.addFlashAttribute("error", "Email không đúng!");
            return "redirect:/users/change-password";
        }
        if (!user.getPassword().equals(currentPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không đúng!");
            return "redirect:/users/change-password";
        }
        // TODO: Kiểm tra OTP nếu cần
        if (!newPassword.equals(confirmNewPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới không khớp!");
            return "redirect:/users/change-password";
        }
        user.setPassword(newPassword);
        userService.saveUser(user);
        session.setAttribute("loggedInUser", user);
        redirectAttributes.addFlashAttribute("toast", "Đổi mật khẩu thành công!");
        return "redirect:/users/profile";
    }
    @Autowired
    private UserService userService;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private FlashcardService flashcardService;

    @Autowired
    private DeckRepository deckRepository;

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
            // Get user from session
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            if (loggedInUser == null) {
                model.addAttribute("error", "Please log in to view your profile.");
                System.out.println("User not logged in");
                return "login/login";
            }

            // Reload user from DB to ensure latest data
            var userOpt = userService.findById(loggedInUser.getId());
            User user = userOpt.orElse(loggedInUser);

            // Xử lý đường dẫn avatarUrl cho view (chuẩn hóa theo WebConfig: /images/avatar/** ánh xạ tới uploads/avatar)
            String avatarUrl = user.getAvatarUrl();
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                if (avatarUrl.startsWith("http") || avatarUrl.startsWith("/images/avatar/")) {
                    // giữ nguyên
                } else if (avatarUrl.startsWith("/uploads/avatar/")) {
                    avatarUrl = "/images/avatar/" + avatarUrl.substring("/uploads/avatar/".length());
                } else if (avatarUrl.startsWith("uploads/avatar/")) {
                    avatarUrl = "/images/avatar/" + avatarUrl.substring("uploads/avatar/".length());
                } else if (avatarUrl.contains("/")) {
                    // Nếu là đường dẫn tuyệt đối hoặc khác, chỉ lấy tên file
                    String fileName = avatarUrl.substring(avatarUrl.lastIndexOf("/") + 1);
                    avatarUrl = "/images/avatar/" + fileName;
                } else {
                    // chỉ là tên file
                    avatarUrl = "/images/avatar/" + avatarUrl;
                }
            }
            user.setAvatarUrl(avatarUrl);
            model.addAttribute("user", user);

            // Get number of decks and flashcards learned
            int deckLearned = quizRepository.findByUserId(user.getId()).size();
            int flashcardLearned = userService.countFlashcardsLearnedByUserId(user.getId());
            int totalFlashcards = flashcardService.getAllFlashcards().size(); // Get total flashcards

            // Time range: last 24 hours
            Date endDate = new Date();
            Date startDate = new Date(endDate.getTime() - 24L * 60 * 60 * 1000); // 24 hours ago

            List<Quiz> quizzes = quizRepository.findByUserAndCreatedAtBetween(user, startDate, endDate);
            List<Deck> decks = deckRepository.findByUserAndCreatedAtBetween(user, startDate, endDate);

            model.addAttribute("deckLearned", deckLearned);
            model.addAttribute("flashcardLearned", flashcardLearned);

            // Calculate correct answer rate
            double correctRate = totalFlashcards > 0 ? ((double) flashcardLearned / totalFlashcards) * 100 : 0;
            model.addAttribute("correctRate", String.format("%.2f", correctRate) + "%");
            model.addAttribute("quizzes", quizzes);
            model.addAttribute("decks", decks);

            return "user/profile";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred: " + e.getMessage());
            return "user/profile";
        }
    }
}