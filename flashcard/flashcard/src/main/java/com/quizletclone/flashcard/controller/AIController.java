// controllers/AIController.java
package com.quizletclone.flashcard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quizletclone.flashcard.service.AIService;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/generate")
    public String generate(@RequestParam String term, @RequestParam String definition) {
        return aiService.generateQuestionFromFlashcard(term, definition);
    }
}
