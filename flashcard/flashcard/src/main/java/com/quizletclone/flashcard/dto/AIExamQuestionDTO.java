package com.quizletclone.flashcard.dto;
import java.util.List;

public class AIExamQuestionDTO {
    private String content;
    private List<AIExamOptionDTO> options;

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public List<AIExamOptionDTO> getOptions() {
        return options;
    }
    public void setOptions(List<AIExamOptionDTO> options) {
        this.options = options;
    }
}
