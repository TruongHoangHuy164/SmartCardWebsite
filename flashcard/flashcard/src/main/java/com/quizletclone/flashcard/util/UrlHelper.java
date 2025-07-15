package com.quizletclone.flashcard.util;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class UrlHelper {

    public static String redirectWithMessage(String path, RedirectAttributes redirectAttributes, String key,
            String message) {
        redirectAttributes.addFlashAttribute(key, message);
        return "redirect:" + path;
    }
}
