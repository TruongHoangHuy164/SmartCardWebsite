package com.quizletclone.flashcard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.quizletclone.flashcard.model.Deck;
import com.quizletclone.flashcard.model.Flashcard;
import com.quizletclone.flashcard.repository.DeckRepository;
import com.quizletclone.flashcard.service.FlashcardService;

@Controller
@RequestMapping("/flashcards")
public class FlashcardViewController {

    @Autowired
    private FlashcardService flashcardService;

    @Autowired
    private DeckRepository deckRepository;

    // Form tạo flashcard mới
    @GetMapping("/create")
    public String showCreateForm(@RequestParam("deckId") Integer deckId, Model model) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new IllegalArgumentException("Deck không tồn tại"));

        Flashcard flashcard = new Flashcard();
        flashcard.setDeck(deck);

        model.addAttribute("flashcard", flashcard);
        return "flashcard/add";
    }

    @PostMapping("/create")
    public String createFlashcard(@ModelAttribute Flashcard flashcard,
            @RequestParam("deckId") Integer deckId,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new IllegalArgumentException("Deck không tồn tại"));

        flashcard.setDeck(deck);

        // TODO: xử lý ảnh ở đây nếu cần

        flashcardService.saveFlashcard(flashcard);
        redirectAttributes.addFlashAttribute("success", "Đã thêm thẻ thành công!");

        return "redirect:/decks/" + deckId;
    }

    // Form chỉnh sửa flashcard
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        Flashcard flashcard = flashcardService.getFlashcardById(id)
                .orElseThrow(() -> new IllegalArgumentException("Flashcard không tồn tại"));

        model.addAttribute("flashcard", flashcard);
        return "flashcard/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateFlashcard(@PathVariable("id") Integer id,
            @ModelAttribute("flashcard") Flashcard updatedCard,
            RedirectAttributes redirectAttributes) {
        Flashcard existing = flashcardService.getFlashcardById(id)
                .orElseThrow(() -> new IllegalArgumentException("Flashcard không tồn tại"));

        existing.setTerm(updatedCard.getTerm());
        existing.setDefinition(updatedCard.getDefinition());
        existing.setImageUrl(updatedCard.getImageUrl());

        flashcardService.saveFlashcard(existing);

        redirectAttributes.addFlashAttribute("success", "Cập nhật thẻ thành công!");
        return "redirect:/decks/" + existing.getDeck().getId();
    }

    @PostMapping("/delete/{id}")
    public String deleteFlashcard(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        Flashcard flashcard = flashcardService.getFlashcardById(id)
                .orElseThrow(() -> new IllegalArgumentException("Flashcard không tồn tại"));

        Integer deckId = flashcard.getDeck().getId();
        flashcardService.deleteFlashcard(id);

        redirectAttributes.addFlashAttribute("success", "Đã xóa thẻ thành công!");
        return "redirect:/decks/" + deckId;
    }
}
