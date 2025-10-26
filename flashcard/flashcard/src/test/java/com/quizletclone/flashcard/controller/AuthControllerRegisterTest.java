
package com.quizletclone.flashcard.controller;

import com.quizletclone.flashcard.model.User;
import com.quizletclone.flashcard.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerRegisterTest {
    @Mock
    private UserService userService;
    @Mock
    private Model model;
    @Mock
    private HttpSession session;
    @Mock
    private RedirectAttributes redirectAttributes;
    @InjectMocks
    private AuthController authController;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
    }

    @Test
    void dangKy_ThanhCong() {
        when(userService.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userService.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        String result = authController.register(user, user.getPassword(), model, session, redirectAttributes);
        assertEquals("redirect:/login", result);
    }

    @Test
    void dangKy_TenDangNhapDaTonTai() {
        when(userService.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        String result = authController.register(user, user.getPassword(), model, session, redirectAttributes);
        assertEquals("redirect:/signup", result);
    }

    @Test
    void dangKy_EmailDaTonTai() {
        when(userService.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userService.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        String result = authController.register(user, user.getPassword(), model, session, redirectAttributes);
        assertEquals("redirect:/signup", result);
    }

    @Test
    void dangKy_MatKhauXacNhanKhongKhop() {
        when(userService.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userService.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        String result = authController.register(user, "wrongpassword", model, session, redirectAttributes);
        assertEquals("redirect:/signup", result);
    }

    @Test
    void dangKy_XacNhanMatKhauNull() {
        when(userService.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userService.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        String result = authController.register(user, null, model, session, redirectAttributes);
        assertEquals("redirect:/signup", result);
    }

    @Test
    void dangKy_EmailNull() {
        user.setEmail(null);
        when(userService.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userService.findByEmail(null)).thenReturn(Optional.empty());
        String result = authController.register(user, user.getPassword(), model, session, redirectAttributes);
        assertEquals("redirect:/login", result);
    }

    @Test
    void dangKy_TenDangNhapNull() {
        user.setUsername(null);
        when(userService.findByUsername(null)).thenReturn(Optional.empty());
        when(userService.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        String result = authController.register(user, user.getPassword(), model, session, redirectAttributes);
        assertEquals("redirect:/login", result);
    }

    @Test
    void dangKy_MatKhauNull() {
        user.setPassword(null);
        when(userService.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userService.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        String result = authController.register(user, null, model, session, redirectAttributes);
        assertEquals("redirect:/signup", result);
    }

    @Test
    void dangKy_ExceptionKhiDangKy() {
        when(userService.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userService.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        doThrow(new RuntimeException("DB error")).when(userService).saveUserWithRole(any(User.class), eq("USER"));
        String result = authController.register(user, user.getPassword(), model, session, redirectAttributes);
        assertEquals("redirect:/signup", result);
    }

    @Test
    void dangKy_SaveUserTraVeNull() {
        when(userService.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userService.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userService.saveUserWithRole(any(User.class), eq("USER"))).thenReturn(null);
        String result = authController.register(user, user.getPassword(), model, session, redirectAttributes);
        assertEquals("redirect:/login", result); // vẫn chuyển về login vì không có check null
    }
}
