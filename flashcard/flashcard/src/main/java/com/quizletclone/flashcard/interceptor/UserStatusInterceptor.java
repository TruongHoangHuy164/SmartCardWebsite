package com.quizletclone.flashcard.interceptor;

import com.quizletclone.flashcard.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserStatusInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object userObj = session.getAttribute("loggedInUser");
            if (userObj instanceof User user) {
                // Check if the user is locked
                if (user.getEnabled() != null && !user.getEnabled()) {
                    // Invalidate session to log them out
                    session.invalidate();
                    // Redirect to a specific error page
                    response.sendRedirect(request.getContextPath() + "/account-locked");
                    return false; // Stop the request
                }
            }
        }
        return true; // Continue the request
    }
} 