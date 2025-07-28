package com.quizletclone.flashcard.service;

import com.quizletclone.flashcard.model.Deck;
import com.quizletclone.flashcard.model.Flashcard;
import com.quizletclone.flashcard.model.User;
import com.quizletclone.flashcard.repository.DeckRepository;
import com.quizletclone.flashcard.repository.FlashcardRepository;
import com.quizletclone.flashcard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PdfImportService {
    @Autowired
    private DeckRepository deckRepository;
    @Autowired
    private FlashcardRepository flashcardRepository;
    @Autowired
    private UserRepository userRepository;

    public int importDecksAndFlashcards(MultipartFile file, Integer userId) throws Exception {
        InputStream inputStream = file.getInputStream();
        PDDocument document = PDDocument.load(inputStream);
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();

        // Parse PDF text
        String[] lines = text.split("\r?\n");
        Deck currentDeck = null;
        int count = 0;
        User user = userRepository.findById(userId).orElse(null);
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("##")) {
                // New deck: ##title;subject;description
                String[] parts = line.substring(2).split(";");
                if (parts.length >= 3) {
                    currentDeck = new Deck();
                    currentDeck.setTitle(parts[0]);
                    currentDeck.setSubject(parts[1]);
                    currentDeck.setDescription(parts[2]);
                    currentDeck.setUser(user);
                    currentDeck.setCreatedAt(new Date());
                    deckRepository.save(currentDeck);
                    count++;
                }
            } else if (currentDeck != null && line.contains(";") && !line.startsWith("##")) {
                // Flashcard: term;definition
                String[] parts = line.split(";");
                if (parts.length >= 2) {
                    Flashcard flashcard = new Flashcard();
                    flashcard.setDeck(currentDeck);
                    flashcard.setTerm(parts[0]);
                    flashcard.setDefinition(parts[1]);
                    flashcard.setCreatedAt(new Date());
                    flashcardRepository.save(flashcard);
                }
            }
        }
        return count;
    }
}
