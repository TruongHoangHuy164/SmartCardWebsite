package com.quizletclone.flashcard.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "flashcards")
public class Flashcard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String term;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String definition;

    @ManyToOne
    @JoinColumn(name = "deck_id")
    private Deck deck;

    @Column(name = "image_url")
    private String imageUrl;
} 