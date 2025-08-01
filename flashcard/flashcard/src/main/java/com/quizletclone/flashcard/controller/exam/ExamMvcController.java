package com.quizletclone.flashcard.controller.exam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.quizletclone.flashcard.model.User;
import com.quizletclone.flashcard.model.exam.Exam;
import com.quizletclone.flashcard.model.exam.ExamAttempt;
import com.quizletclone.flashcard.repository.exam.ExamAttemptRepository;
import com.quizletclone.flashcard.service.exam.ExamExportService;
import com.quizletclone.flashcard.service.exam.ExamQuestionService;
import com.quizletclone.flashcard.service.exam.ExamService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/exams")
public class ExamMvcController {
    @Autowired
    private com.quizletclone.flashcard.service.AIService aiService;
    @Autowired
    private com.quizletclone.flashcard.service.exam.ExamOptionService examOptionService;

    @PostMapping("/{id}/ai-import")
    public String aiImportExam(@PathVariable Long id,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            HttpSession session, RedirectAttributes redirectAttributes) {
        com.quizletclone.flashcard.model.User user = (com.quizletclone.flashcard.model.User) session
                .getAttribute("loggedInUser");
        java.util.Optional<com.quizletclone.flashcard.model.exam.Exam> examOpt = examService.getExamById(id);
        if (examOpt.isEmpty() || user == null || !examOpt.get().getCreatedBy().equals(user.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền import đề này!");
            return "redirect:/exams/" + id;
        }
        String text = "";
        try {
            String filename = file.getOriginalFilename().toLowerCase();
            if (filename.endsWith(".pdf")) {
                try (org.apache.pdfbox.pdmodel.PDDocument document = org.apache.pdfbox.pdmodel.PDDocument
                        .load(file.getInputStream())) {
                    org.apache.pdfbox.text.PDFTextStripper stripper = new org.apache.pdfbox.text.PDFTextStripper();
                    text = stripper.getText(document);
                }
            } else if (filename.endsWith(".doc") || filename.endsWith(".docx")) {
                org.apache.poi.xwpf.usermodel.XWPFDocument doc = new org.apache.poi.xwpf.usermodel.XWPFDocument(
                        file.getInputStream());
                StringBuilder sb = new StringBuilder();
                for (org.apache.poi.xwpf.usermodel.XWPFParagraph p : doc.getParagraphs()) {
                    sb.append(p.getText()).append("\n");
                }
                text = sb.toString();
            } else {
                redirectAttributes.addFlashAttribute("error", "Định dạng file không hỗ trợ!");
                return "redirect:/exams/" + id;
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi đọc file: " + e.getMessage());
            return "redirect:/exams/" + id;
        }
        var questions = aiService.analyzeExamQuestions(text);
        if (questions.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "AI không phân tích được đề thi hoặc file không đúng định dạng!");
            return "redirect:/exams/" + id;
        }
        com.quizletclone.flashcard.model.exam.Exam exam = examOpt.get();
        int order = examQuestionService.getQuestionsByExam(exam).size() + 1;
        for (var qDto : questions) {
            com.quizletclone.flashcard.model.exam.ExamQuestion question = new com.quizletclone.flashcard.model.exam.ExamQuestion();
            question.setExam(exam);
            question.setContent(qDto.getContent());
            question.setQuestionOrder(order++);
            question = examQuestionService.addQuestion(question);
            for (var optDto : qDto.getOptions()) {
                com.quizletclone.flashcard.model.exam.ExamOption option = new com.quizletclone.flashcard.model.exam.ExamOption();
                option.setQuestion(question);
                option.setContent(optDto.getContent());
                option.setCorrect(optDto.isCorrect());
                examOptionService.addOption(option);
            }
        }
        redirectAttributes.addFlashAttribute("success", "Đã import thành công " + questions.size() + " câu hỏi!");
        return "redirect:/exams/" + id;
    }

    @Autowired
    private ExamService examService;

    @Autowired
    private ExamQuestionService examQuestionService;

    @Autowired
    private ExamExportService examExportService;

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    @GetMapping
    public String listExams(Model model) {
        model.addAttribute("exams", examService.getPublicExams());
        return "exam/list";
    }

    @GetMapping("/create")
    public String createExamForm(Model model) {
        model.addAttribute("exam", new Exam());
        return "exam/create";
    }

    @PostMapping("/create")
    public String createExam(@ModelAttribute Exam exam, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null) {
            exam.setCreatedBy(user.getUsername());
        }
        examService.createExam(exam);
        return "redirect:/exams";
    }

    @GetMapping("/my")
    public String myExams(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null)
            return "redirect:/login";
        model.addAttribute("exams", examService.getExamsByCreator(user.getUsername()));
        return "exam/list";
    }

    @GetMapping("/{id}")
    public String examDetail(@PathVariable Long id, Model model) {
        Optional<Exam> exam = examService.getExamById(id);
        if (exam.isPresent()) {
            model.addAttribute("exam", exam.get());
            model.addAttribute("questions", examQuestionService.getQuestionsByExam(exam.get()));
            return "exam/detail";
        }
        return "error/404";
    }

    @GetMapping("/code/{code}")
    public String examDetailByCode(@PathVariable String code, Model model) {
        Optional<Exam> exam = examService.getExamByCode(code);
        if (exam.isPresent()) {
            model.addAttribute("exam", exam.get());
            model.addAttribute("questions", examQuestionService.getQuestionsByExam(exam.get()));
            return "exam/detail";
        }
        return "error/404";
    }

    @PostMapping("/toggle-public/{id}")
    public String togglePublic(@PathVariable Long id, @RequestParam(required = false) Boolean isPublic,
            HttpSession session) {
        Optional<Exam> examOpt = examService.getExamById(id);
        User user = (User) session.getAttribute("loggedInUser");
        if (examOpt.isEmpty() || user == null || !examOpt.get().getCreatedBy().equals(user.getUsername())) {
            return "error/404";
        }
        Exam exam = examOpt.get();
        exam.setPublic(isPublic != null && isPublic);
        examService.createExam(exam);
        return "redirect:/exams/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteExam(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedInUser");
        Optional<Exam> examOpt = examService.getExamById(id);
        if (examOpt.isPresent() && user != null && examOpt.get().getCreatedBy().equals(user.getUsername())) {
            examService.deleteExam(id);
            redirectAttributes.addFlashAttribute("success", "Xóa đề thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền xóa đề này!");
        }
        return "redirect:/exams";
    }

    @GetMapping("/{id}/questions-count")
    @ResponseBody
    public Map<String, Object> getQuestionsCount(@PathVariable Long id) {
        Map<String, Object> res = new HashMap<>();
        Optional<Exam> examOpt = examService.getExamById(id);
        res.put("count", examOpt.map(e -> examQuestionService.getQuestionsByExam(e).size()).orElse(0));
        return res;
    }

    @GetMapping("/{id}/export-results")
    public ResponseEntity<InputStreamResource> exportExamResults(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        Optional<Exam> examOpt = examService.getExamById(id);
        if (examOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Exam exam = examOpt.get();
        // Kiểm tra quyền - chỉ người tạo đề mới có quyền xuất bảng điểm
        if (!exam.getCreatedBy().equals(user.getUsername())) {
            return ResponseEntity.status(403).build();
        }

        try {
            InputStreamResource resource = new InputStreamResource(examExportService.exportExamResults(exam));

            String filename = "bang-diem-" + exam.getTitle().replaceAll("[^a-zA-Z0-9\\s-]", "") + ".xlsx";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType
                            .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}/attempt-history")
    public String examAttemptHistory(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Exam> examOpt = examService.getExamById(id);
        if (examOpt.isEmpty()) {
            // Thêm exam null để template hiển thị thông báo lỗi
            model.addAttribute("exam", null);
            model.addAttribute("attempts", java.util.Collections.emptyList());
            return "exam/attempt_history";
        }

        Exam exam = examOpt.get();
        // Kiểm tra quyền - chỉ người tạo đề mới có quyền xem lịch sử
        if (!exam.getCreatedBy().equals(user.getUsername())) {
            // Thêm exam null để template hiển thị thông báo lỗi
            model.addAttribute("exam", null);
            model.addAttribute("attempts", java.util.Collections.emptyList());
            return "exam/attempt_history";
        }

        // Lấy danh sách attempts cho exam này
        List<ExamAttempt> attempts = examAttemptRepository.findByExam_IdOrderBySubmittedAtDesc(exam.getId());

        // Tính toán thống kê
        double averageScore = calculateAverageScore(attempts);
        double averageCorrect = calculateAverageCorrect(attempts);
        double averagePercentage = calculateAveragePercentage(attempts, exam.getTotalQuestions());

        model.addAttribute("exam", exam);
        model.addAttribute("attempts", attempts);
        model.addAttribute("averageScore", averageScore);
        model.addAttribute("averageCorrect", averageCorrect);
        model.addAttribute("averagePercentage", averagePercentage);
        return "exam/attempt_history";
    }

    // Static methods for calculating statistics
    public static double calculateAverageScore(List<ExamAttempt> attempts) {
        if (attempts.isEmpty())
            return 0.0;
        return attempts.stream().mapToInt(ExamAttempt::getScore).average().orElse(0.0);
    }

    public static double calculateAverageCorrect(List<ExamAttempt> attempts) {
        if (attempts.isEmpty())
            return 0.0;
        return attempts.stream().mapToInt(ExamAttempt::getCorrectCount).average().orElse(0.0);
    }

    public static double calculateAveragePercentage(List<ExamAttempt> attempts, int totalQuestions) {
        if (attempts.isEmpty() || totalQuestions == 0)
            return 0.0;
        return attempts.stream()
                .mapToDouble(a -> (double) a.getCorrectCount() / totalQuestions * 100)
                .average().orElse(0.0);
    }
}