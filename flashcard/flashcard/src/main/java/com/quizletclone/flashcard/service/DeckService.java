package com.quizletclone.flashcard.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quizletclone.flashcard.model.Deck;
import com.quizletclone.flashcard.model.User;
import com.quizletclone.flashcard.repository.DeckRepository;

@Service
public class DeckService {
    @Autowired
    private DeckRepository deckRepository;

    public List<Deck> getAllDecksSplit(User user) {
        if (user != null) {
            List<Deck> myDecks = deckRepository.findByUser(user);
            List<Deck> otherDecks = deckRepository.findByUserNot(user);
            List<Deck> all = new ArrayList<>();
            all.addAll(myDecks);
            all.addAll(otherDecks);
            return all;
        } else {
            return deckRepository.findAll();
        }
    }

    public List<Deck> getDecksByUser(User user) {
        return deckRepository.findByUser(user);
    }

    // Lấy tất cả Deck không phải của user hiện tại
    public List<Deck> getDecksNotByUser(User user) {
        return deckRepository.findByUserNot(user);
    }

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
            if (sub.equalsIgnoreCase("Khác")) {
                // Danh sách các môn học chuẩn
                List<String> predefinedSubjects = List.of(
                        "Toán học", "Vật lý", "Hóa học", "Sinh học",
                        "Lịch sử", "Địa lý", "Văn học", "Tiếng Anh",
                        "Tiếng Việt", "Công nghệ");

                result = result.stream()
                        .filter(deck -> !predefinedSubjects.contains(deck.getSubject()))
                        .collect(Collectors.toList());
            } else if (!sub.equalsIgnoreCase("Tất cả")) {
                // Lọc theo môn cụ thể nếu không phải là "Tất cả"
                result = result.stream()
                        .filter(deck -> deck.getSubject().equalsIgnoreCase(sub))
                        .collect(Collectors.toList());
            }
            // Nếu là "Tất cả" thì giữ nguyên không lọc theo subject
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
            if (sub.equalsIgnoreCase("Khác")) {
                // Danh sách các môn học chuẩn
                List<String> predefinedSubjects = List.of(
                        "Toán học", "Vật lý", "Hóa học", "Sinh học",
                        "Lịch sử", "Địa lý", "Văn học", "Tiếng Anh",
                        "Tiếng Việt", "Công nghệ");

                result = result.stream()
                        .filter(deck -> !predefinedSubjects.contains(deck.getSubject()))
                        .collect(Collectors.toList());
            } else if (!sub.equalsIgnoreCase("Tất cả")) {
                // Lọc theo môn cụ thể nếu không phải là "Tất cả"
                result = result.stream()
                        .filter(deck -> deck.getSubject().equalsIgnoreCase(sub))
                        .collect(Collectors.toList());
            }
            // Nếu là "Tất cả" thì giữ nguyên không lọc theo subject
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