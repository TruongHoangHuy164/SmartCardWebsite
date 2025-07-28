package com.quizletclone.flashcard.controller.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.quizletclone.flashcard.model.User;
import com.quizletclone.flashcard.service.PdfImportService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class ImportPdfController {
    @Autowired
    private PdfImportService pdfImportService;

    @GetMapping("/import-pdf")
    public String showImportPdfPage() {
        return "admin/import_pdf"; // Trả về view để người dùng có thể tải lên file PDF
    }

    @PostMapping("/import-pdf")
    public String importPdf(@RequestParam("file") MultipartFile file, HttpSession session, Model model) {
        try {
            // Lấy userId từ session hoặc gán mặc định (ví dụ: 1)
            User user = (User) session.getAttribute("loggedInUser");
            Integer userId = user != null ? user.getId() : null;
            if (userId == null)
                userId = 1;
            int importedCount = pdfImportService.importDecksAndFlashcards(file, userId);
            model.addAttribute("message", "Đã import thành công " + importedCount + " bộ thẻ và thẻ từ PDF!");
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi import PDF: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
}
