package com.quizletclone.flashcard.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quizletclone.flashcard.model.User;
import com.quizletclone.flashcard.service.UserService;

import com.quizletclone.flashcard.dto.EditProfileRequest;
import com.quizletclone.flashcard.dto.ChangePasswordRequest;
import com.quizletclone.flashcard.service.OtpMailService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private OtpMailService otpMailService;

    private Map<String, String> otpStore = new HashMap<>(); // email -> otp

    //private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Integer id) {
        return userService.findById(id);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
    }

    // Chỉnh sửa hồ sơ
    @PutMapping("/profile")
    public ResponseEntity<?> editProfile(@RequestParam Integer userId, @RequestBody EditProfileRequest req) {
        User user = userService.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setAvatarUrl(req.getAvatarUrl());
        userService.saveUser(user);
        return ResponseEntity.ok("Cập nhật hồ sơ thành công");
    }

    // Gửi OTP về email để xác nhận đổi mật khẩu
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestParam String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStore.put(email, otp);
        otpMailService.sendOtpEmail(email, otp);
        return ResponseEntity.ok("OTP đã được gửi về email");
    }

    // Đổi mật khẩu với xác thực OTP
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest req) {
        User user = userService.findByEmail(req.getEmail()).orElse(null);
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        if (!req.getCurrentPassword().equals(user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mật khẩu hiện tại không đúng");
        }
        String otp = otpStore.get(req.getEmail());
        if (otp == null || !otp.equals(req.getOtp())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP không hợp lệ hoặc đã hết hạn");
        }
        user.setPassword(req.getNewPassword());
        userService.saveUser(user);
        otpStore.remove(req.getEmail());
        return ResponseEntity.ok("Đổi mật khẩu thành công");
    }
    
} 
