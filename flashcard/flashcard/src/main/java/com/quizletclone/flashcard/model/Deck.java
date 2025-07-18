package com.quizletclone.flashcard.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "decks")
public class Deck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String title;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String subject;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_public")
    private Boolean isPublic = true;

    @Column(name = "created_at")
    private Date createdAt;

    @OneToMany(mappedBy = "deck", fetch = FetchType.LAZY)
    private List<Flashcard> flashcards;

    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Quiz> quizzes;
}