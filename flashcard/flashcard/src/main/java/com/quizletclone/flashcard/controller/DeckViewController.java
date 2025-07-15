package com.quizletclone.flashcard.controller;

import com.quizletclone.flashcard.model.Deck;
import com.quizletclone.flashcard.service.DeckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/decks")
public class DeckViewController {
    @Autowired
    private DeckService deckService;

    @GetMapping
    public String listDecks(Model model) {
        List<Deck> decks = deckService.getAllDecks();
        model.addAttribute("decks", decks);
        return "deck/list";
    }

    @GetMapping("/detail/{id}")
    public String deckDetail(@PathVariable Integer id, Model model) {
        deckService.getDeckById(id).ifPresent(deck -> model.addAttribute("deck", deck));
        return "deck/detail";
    }

    @GetMapping("/form")
    public String deckForm(@RequestParam(value = "id", required = false) Integer id, Model model) {
        if (id != null) {
            deckService.getDeckById(id).ifPresent(deck -> model.addAttribute("deck", deck));
        } else {
            model.addAttribute("deck", new com.quizletclone.flashcard.model.Deck());
        }
        return "deck/form";
    }

    @PostMapping("/save")
    public String saveDeck(@ModelAttribute("deck") com.quizletclone.flashcard.model.Deck deck, RedirectAttributes redirectAttributes) {
        deckService.saveDeck(deck);
        redirectAttributes.addFlashAttribute("toast", "Lưu bộ đề thành công!");
        return "redirect:/decks";
    }
} 