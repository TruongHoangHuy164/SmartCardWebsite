package com.quizletclone.flashcard.util;

import com.quizletclone.flashcard.model.Deck;
import com.quizletclone.flashcard.model.User;
import org.springframework.stereotype.Component;

@Component("perm") // Gọi được trong Thymeleaf bằng @perm
public class PermissionUtils {

    public boolean isOwner(User user, Deck deck) {
        return user != null &&
                deck != null &&
                deck.getUser() != null &&
                user.getId().equals(deck.getUser().getId());
    }

    public boolean isAdmin(User user) {
        return user != null &&
                user.getRole() != null &&
                "ADMIN".equalsIgnoreCase(user.getRole().getName());
    }

    public boolean isOwnerOrAdmin(User user, Deck deck) {
        return isOwner(user, deck) || isAdmin(user);
    }
}
