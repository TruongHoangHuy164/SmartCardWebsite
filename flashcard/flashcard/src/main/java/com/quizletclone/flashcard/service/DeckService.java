package com.quizletclone.flashcard.service;

import com.quizletclone.flashcard.model.Deck;
import com.quizletclone.flashcard.repository.DeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeckService {
    @Autowired
    private DeckRepository deckRepository;

    public List<Deck> getAllDecks() {
        return deckRepository.findAll();
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
} 