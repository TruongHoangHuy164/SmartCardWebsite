package com.quizletclone.flashcard.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quizletclone.flashcard.model.Deck;
import com.quizletclone.flashcard.repository.DeckRepository;

@Service
public class DeckService {
    @Autowired
    private DeckRepository deckRepository;

    public List<Deck> getAllDecks() {
        return deckRepository.findAll();
    }

    public List<Deck> getPublicDecks() {
        return deckRepository.findByIsPublicTrue();
    }

    public Optional<Deck> getDeckById(Integer id) {
        return deckRepository.findById(id);
    }

    public Deck saveDeck(Deck deck) {
        return deckRepository.save(deck);
    }

    public void deleteDeck(Integer id) {
        deckRepository.deleteById(id);
    }

    public List<Deck> searchAndSortDecks(String keyword, String sort, String sub) {
        List<Deck> result = deckRepository.findAll();

        if (keyword != null && !keyword.isEmpty()) {
            result = result.stream()
                    .filter(deck -> deck.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (sub != null && !sub.isEmpty()) {
            result = result.stream()
                    .filter(deck -> deck.getSubject().equalsIgnoreCase(sub))
                    .collect(Collectors.toList());
        }

        if (sort != null) {
            switch (sort) {
                case "az" -> result.sort(Comparator.comparing(deck -> deck.getTitle().toLowerCase()));
                case "za" -> result.sort(Comparator.comparing((Deck deck) -> deck.getTitle().toLowerCase()).reversed());
                case "newest" -> result.sort(Comparator.comparing(Deck::getCreatedAt).reversed());
                case "oldest" -> result.sort(Comparator.comparing(Deck::getCreatedAt));
            }
        }

        return result;
    }

    public List<Deck> searchAndSortPublicDecks(String keyword, String sort, String sub) {
        List<Deck> result = deckRepository.findByIsPublicTrue();

        if (keyword != null && !keyword.isEmpty()) {
            result = result.stream()
                    .filter(deck -> deck.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (sub != null && !sub.isEmpty()) {
            result = result.stream()
                    .filter(deck -> deck.getSubject().equalsIgnoreCase(sub))
                    .collect(Collectors.toList());
        }

        if (sort != null) {
            switch (sort) {
                case "az" -> result.sort(Comparator.comparing(deck -> deck.getTitle().toLowerCase()));
                case "za" -> result.sort(Comparator.comparing((Deck deck) -> deck.getTitle().toLowerCase()).reversed());
                case "newest" -> result.sort(Comparator.comparing(Deck::getCreatedAt).reversed());
                case "oldest" -> result.sort(Comparator.comparing(Deck::getCreatedAt));
            }
        }

        return result;
    }

}