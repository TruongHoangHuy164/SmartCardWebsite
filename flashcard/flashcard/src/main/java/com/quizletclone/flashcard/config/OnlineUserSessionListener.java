package com.quizletclone.flashcard.config;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OnlineUserSessionListener implements HttpSessionListener {
    public static final Set<Integer> ONLINE_USER_IDS = ConcurrentHashMap.newKeySet();

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        // Không làm gì khi tạo session
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        Object userObj = session.getAttribute("loggedInUser");
        if (userObj instanceof com.quizletclone.flashcard.model.User user) {
            ONLINE_USER_IDS.remove(user.getId());
        }
    }

    // Hàm tiện ích: gọi khi login thành công
    public static void addOnlineUser(Integer userId) {
        ONLINE_USER_IDS.add(userId);
    }
    // Hàm tiện ích: gọi khi logout
    public static void removeOnlineUser(Integer userId) {
        ONLINE_USER_IDS.remove(userId);
    }
} 