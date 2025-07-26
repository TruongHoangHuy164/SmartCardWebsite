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