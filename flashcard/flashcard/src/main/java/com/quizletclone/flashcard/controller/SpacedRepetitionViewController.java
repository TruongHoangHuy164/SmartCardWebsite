package com.quizletclone.flashcard.controller;

import com.quizletclone.flashcard.model.SpacedRepetition;
import com.quizletclone.flashcard.service.SpacedRepetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
@RequestMapping("/spaced-repetitions")
public class SpacedRepetitionViewController {
    @Autowired
    private SpacedRepetitionService spacedRepetitionService;

    @GetMapping
    public String listSpacedRepetitions(Model model) {
        List<SpacedRepetition> spacedRepetitions = spacedRepetitionService.getAllSpacedRepetitions();
        model.addAttribute("spacedRepetitions", spacedRepetitions);
        return "spacedrepetition/list";
    }

    @GetMapping("/detail/{id}")
    public String spacedRepetitionDetail(@PathVariable Integer id, Model model) {
        spacedRepetitionService.getSpacedRepetitionById(id).ifPresent(sr -> model.addAttribute("spacedRepetition", sr));
        return "spacedrepetition/detail";
    }

    @GetMapping("/form")
    public String spacedRepetitionForm(@RequestParam(value = "id", required = false) Integer id, Model model) {
        if (id != null) {
            spacedRepetitionService.getSpacedRepetitionById(id).ifPresent(sr -> model.addAttribute("spacedRepetition", sr));
        } else {
            model.addAttribute("spacedRepetition", new com.quizletclone.flashcard.model.SpacedRepetition());
        }
        return "spacedrepetition/form";
    }

    @PostMapping("/save")
    public String saveSpacedRepetition(@ModelAttribute("spacedRepetition") com.quizletclone.flashcard.model.SpacedRepetition spacedRepetition) {
        spacedRepetitionService.saveSpacedRepetition(spacedRepetition);
        return "redirect:/spaced-repetitions";
    }
} 