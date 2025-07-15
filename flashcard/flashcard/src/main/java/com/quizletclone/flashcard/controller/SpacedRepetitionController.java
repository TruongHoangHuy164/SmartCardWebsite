package com.quizletclone.flashcard.controller;

import com.quizletclone.flashcard.model.SpacedRepetition;
import com.quizletclone.flashcard.service.SpacedRepetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/spaced-repetitions")
public class SpacedRepetitionController {
    @Autowired
    private SpacedRepetitionService spacedRepetitionService;

    @GetMapping
    public List<SpacedRepetition> getAllSpacedRepetitions() {
        return spacedRepetitionService.getAllSpacedRepetitions();
    }

    @GetMapping("/{id}")
    public Optional<SpacedRepetition> getSpacedRepetitionById(@PathVariable Integer id) {
        return spacedRepetitionService.getSpacedRepetitionById(id);
    }

    @PostMapping
    public SpacedRepetition createSpacedRepetition(@RequestBody SpacedRepetition spacedRepetition) {
        return spacedRepetitionService.saveSpacedRepetition(spacedRepetition);
    }

    @DeleteMapping("/{id}")
    public void deleteSpacedRepetition(@PathVariable Integer id) {
        spacedRepetitionService.deleteSpacedRepetition(id);
    }
} 