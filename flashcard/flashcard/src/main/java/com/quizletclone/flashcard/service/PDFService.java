package com.quizletclone.flashcard.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.regex.*;

@Service
public class PDFService {

    public String processPDF(MultipartFile file) throws IOException {
        PDDocument document = PDDocument.load(file.getInputStream());
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();

        StringBuilder result = new StringBuilder();

        String[] lines = text.split("\n");
        Pattern questionPattern = Pattern.compile("^Question\\s*\\d+:.*");
        Pattern answerPattern = Pattern.compile("^[A-D]\\..*");

        boolean inParagraph = false;

        for (String line : lines) {
            line = line.trim();

            // Xử lý câu hỏi
            if (questionPattern.matcher(line).matches()) {
                result.append("\n").append(line).append("\n");
                inParagraph = false;
                continue;
            }

            // Xử lý đáp án: mỗi đáp án A.–D. xuống dòng riêng
            if (line.matches(".*[A-D]\\..*")) {
                Pattern splitAnswers = Pattern.compile("(?<=\\s|^)([A-D]\\.\\s*[^A-D]*)");
                Matcher m = splitAnswers.matcher(line);
                while (m.find()) {
                    String answer = m.group(1).trim();
                    if (!answer.isEmpty()) {
                        result.append(answer).append("\n");
                    }
                }
                continue;
            }

            // Gom đoạn văn
            if (!line.isEmpty() && line.split(" ").length > 10) {
                if (!inParagraph) {
                    result.append("Paragraph: ");
                    inParagraph = true;
                }
                result.append(line).append(" ");
            } else {
                if (inParagraph) {
                    result.append("\n");
                    inParagraph = false;
                }
            }
        }

        return result.toString().trim();
    }
}
