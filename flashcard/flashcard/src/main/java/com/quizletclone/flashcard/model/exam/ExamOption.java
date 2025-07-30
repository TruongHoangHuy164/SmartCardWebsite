package com.quizletclone.flashcard.model.exam;

import jakarta.persistence.*;



@Entity
public class ExamOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "question_id")
    private ExamQuestion question;
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;
    private boolean isCorrect;
    // getter, setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExamQuestion getQuestion() {
        return question;
    }

    public void setQuestion(ExamQuestion question) {
        this.question = question;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}