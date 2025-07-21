package com.quizletclone.flashcard.model.exam;

import jakarta.persistence.*;



@Entity
public class ExamAttemptAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "attempt_id")
    private ExamAttempt attempt;
    @ManyToOne
    @JoinColumn(name = "question_id")
    private ExamQuestion question;
    @ManyToOne
    @JoinColumn(name = "selected_option_id")
    private ExamOption selectedOption;
    private boolean isCorrect;
    // getter, setter

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public ExamAttempt getAttempt() {
        return attempt;
    }
    public void setAttempt(ExamAttempt attempt) {
        this.attempt = attempt;
    }
    public ExamQuestion getQuestion() {
        return question;
    }
    public void setQuestion(ExamQuestion question) {
        this.question = question;
    }
    public ExamOption getSelectedOption() {
        return selectedOption;
    }
    public void setSelectedOption(ExamOption selectedOption) {
        this.selectedOption = selectedOption;
    }
    public boolean isCorrect() {
        return isCorrect;
    }
    public void setCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}