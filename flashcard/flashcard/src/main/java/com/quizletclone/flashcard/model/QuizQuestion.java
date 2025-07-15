package com.quizletclone.flashcard.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "quiz_questions")
public class QuizQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "flashcard_id")
    private Flashcard flashcard;

    @Column(name = "question_text", columnDefinition = "NVARCHAR(MAX)")
    private String questionText;

    @Column(name = "correct_answer", columnDefinition = "NVARCHAR(MAX)")
    private String correctAnswer;

    @Column(name = "user_answer", columnDefinition = "NVARCHAR(MAX)")
    private String userAnswer;

    @Column(name = "is_correct")
    private Boolean isCorrect;
} 