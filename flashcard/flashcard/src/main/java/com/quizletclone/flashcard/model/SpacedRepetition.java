package com.quizletclone.flashcard.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "spaced_repetition")
public class SpacedRepetition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "flashcard_id")
    private Flashcard flashcard;

    @Column(name = "last_reviewed")
    private Date lastReviewed;

    @Column(name = "next_review")
    private Date nextReview;

    @Column(name = "interval_days")
    private Integer intervalDays;

    @Column(name = "ease_factor")
    private Float easeFactor;
} 