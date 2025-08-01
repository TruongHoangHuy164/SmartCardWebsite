package com.quizletclone.flashcard.dto;

public class AIExamOptionDTO {
    private String content;
    private boolean correct;

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public boolean isCorrect() {
        return correct;
    }
    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
