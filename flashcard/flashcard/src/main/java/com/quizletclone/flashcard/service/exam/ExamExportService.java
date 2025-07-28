package com.quizletclone.flashcard.service.exam;

import com.quizletclone.flashcard.model.exam.Exam;
import com.quizletclone.flashcard.model.exam.ExamAttempt;
import com.quizletclone.flashcard.repository.exam.ExamAttemptRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExamExportService {

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    public ByteArrayInputStream exportExamResults(Exam exam) throws IOException {
        List<ExamAttempt> attempts = examAttemptRepository.findByExam_IdOrderBySubmittedAtDesc(exam.getId());

        // Debug: In ra thông tin thời gian của các attempts
        System.out.println("=== DEBUG: Thông tin thời gian của các attempts ===");
        for (ExamAttempt attempt : attempts) {
            System.out.println("Attempt ID: " + attempt.getId() +
                    ", User: " + attempt.getUserId() +
                    ", StartedAt: " + attempt.getStartedAt() +
                    ", SubmittedAt: " + attempt.getSubmittedAt());

            // Tính và in ra thời gian làm bài
            if (attempt.getStartedAt() != null && attempt.getSubmittedAt() != null) {
                java.time.Duration duration = java.time.Duration.between(attempt.getStartedAt(),
                        attempt.getSubmittedAt());
                System.out.println("  Duration: " + duration.toHours() + "h " + duration.toMinutesPart() + "m");
            }
        }
        System.out.println("=== END DEBUG ===");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Bảng điểm - " + exam.getTitle());

            // Tạo style cho header
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);

            // Tạo style cho dữ liệu
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);

            // Tạo header
            Row headerRow = sheet.createRow(0);
            String[] headers = { "STT", "Tên học sinh", "Điểm số", "Số câu đúng", "Tổng số câu", "Tỷ lệ đúng (%)",
                    "Thời gian bắt đầu", "Thời gian nộp bài", "Thời gian làm bài" };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 15 * 256); // Đặt độ rộng cột
            }

            // Thêm dữ liệu
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            for (int i = 0; i < attempts.size(); i++) {
                ExamAttempt attempt = attempts.get(i);
                Row row = sheet.createRow(i + 1);

                row.createCell(0).setCellValue(i + 1); // STT
                row.createCell(1).setCellValue(attempt.getUserId()); // Tên học sinh
                row.createCell(2).setCellValue(attempt.getScore()); // Điểm số
                row.createCell(3).setCellValue(attempt.getCorrectCount()); // Số câu đúng
                row.createCell(4).setCellValue(exam.getTotalQuestions()); // Tổng số câu

                // Tính tỷ lệ đúng
                double percentage = exam.getTotalQuestions() > 0
                        ? (double) attempt.getCorrectCount() / exam.getTotalQuestions() * 100
                        : 0;
                row.createCell(5).setCellValue(Math.round(percentage * 100.0) / 100.0); // Tỷ lệ đúng

                // Thời gian bắt đầu và nộp bài - sử dụng logic mới
                String startedAtStr = "N/A";
                String submittedAtStr = "N/A";

                if (attempt.getSubmittedAt() != null && exam.getDuration() > 0 && attempt.getRemainingTime() != null) {
                    // Tính thời gian bắt đầu = thời gian nộp bài - (thời gian đề - thời gian còn
                    // lại)
                    int timeUsed = exam.getDuration() - attempt.getRemainingTime();
                    java.time.LocalDateTime calculatedStartTime = attempt.getSubmittedAt().minusMinutes(timeUsed);
                    startedAtStr = calculatedStartTime.format(formatter);
                    System.out.println("  Calculated StartedAt: " + startedAtStr);
                } else if (attempt.getStartedAt() != null) {
                    // Fallback: sử dụng startedAt nếu có
                    startedAtStr = attempt.getStartedAt().format(formatter);
                    System.out.println("  Using StartedAt: " + startedAtStr);
                }

                if (attempt.getSubmittedAt() != null) {
                    submittedAtStr = attempt.getSubmittedAt().format(formatter);
                    System.out.println("  SubmittedAt formatted: " + submittedAtStr);
                }

                row.createCell(6).setCellValue(startedAtStr); // Thời gian bắt đầu
                row.createCell(7).setCellValue(submittedAtStr); // Thời gian nộp bài

                // Thời gian làm bài = submittedAt - startedAt (hoặc submittedAt -
                // calculatedStartTime)
                String durationText = "N/A";
                if (attempt.getSubmittedAt() != null && ((attempt.getRemainingTime() != null && exam.getDuration() > 0)
                        || attempt.getStartedAt() != null)) {
                    java.time.LocalDateTime startTime = (attempt.getRemainingTime() != null && exam.getDuration() > 0)
                            ? attempt.getSubmittedAt().minusMinutes(exam.getDuration() - attempt.getRemainingTime())
                            : attempt.getStartedAt();
                    java.time.Duration duration = java.time.Duration.between(startTime, attempt.getSubmittedAt());
                    long hours = duration.toHours();
                    long minutes = duration.toMinutesPart();
                    if (duration.toMinutes() >= 60) {
                        durationText = hours + "h " + minutes + "m";
                    } else {
                        durationText = duration.toMinutes() + "m";
                    }
                }
                row.createCell(8).setCellValue(durationText); // Thời gian làm bài

                // Áp dụng style cho tất cả các ô
                for (int j = 0; j < 9; j++) {
                    row.getCell(j).setCellStyle(dataStyle);
                }
            }

            // Thêm thống kê tổng quan
            int totalRows = attempts.size() + 3;
            Row statsRow1 = sheet.createRow(totalRows);
            statsRow1.createCell(0).setCellValue("THỐNG KÊ TỔNG QUAN");
            statsRow1.getCell(0).setCellStyle(headerStyle);

            Row statsRow2 = sheet.createRow(totalRows + 1);
            statsRow2.createCell(0).setCellValue("Tổng số học sinh tham gia:");
            statsRow2.createCell(1).setCellValue(attempts.size());

            if (!attempts.isEmpty()) {
                double avgScore = attempts.stream().mapToInt(ExamAttempt::getScore).average().orElse(0);
                double avgCorrect = attempts.stream().mapToInt(ExamAttempt::getCorrectCount).average().orElse(0);
                double avgPercentage = attempts.stream()
                        .mapToDouble(a -> (double) a.getCorrectCount() / exam.getTotalQuestions() * 100)
                        .average().orElse(0);

                Row statsRow3 = sheet.createRow(totalRows + 2);
                statsRow3.createCell(0).setCellValue("Điểm trung bình:");
                statsRow3.createCell(1).setCellValue(Math.round(avgScore * 100.0) / 100.0);

                Row statsRow4 = sheet.createRow(totalRows + 3);
                statsRow4.createCell(0).setCellValue("Số câu đúng trung bình:");
                statsRow4.createCell(1).setCellValue(Math.round(avgCorrect * 100.0) / 100.0);

                Row statsRow5 = sheet.createRow(totalRows + 4);
                statsRow5.createCell(0).setCellValue("Tỷ lệ đúng trung bình (%):");
                statsRow5.createCell(1).setCellValue(Math.round(avgPercentage * 100.0) / 100.0);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        }
    }
}