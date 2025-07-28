package com.quizletclone.flashcard.service.exam;

import com.quizletclone.flashcard.model.exam.ExamOption;
import com.quizletclone.flashcard.model.exam.ExamQuestion;
import com.quizletclone.flashcard.repository.exam.ExamOptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExamOptionService {
    @Autowired
    private ExamOptionRepository examOptionRepository;

    public ExamOption addOption(ExamOption option) {
        return examOptionRepository.save(option);
    }

    public List<ExamOption> getOptionsByQuestion(ExamQuestion question) {
        return examOptionRepository.findAll().stream()
                .filter(o -> o.getQuestion().getId().equals(question.getId()))
                .toList();
    }

    public Optional<ExamOption> getById(Long id) {
        return examOptionRepository.findById(id);
    }

    public void deleteOption(Long id) {
        examOptionRepository.deleteById(id);
    }
}