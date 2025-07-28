package com.quizletclone.flashcard.controller.Admin;

import com.quizletclone.flashcard.service.PDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PDFController {

    @Autowired
    private PDFService pdfService;

    @GetMapping("/admin/upload")
    public String index() {
        return "admin/pdf/upload";
    }

    @PostMapping("/admin/upload")
    public String handleUpload(@RequestParam("file") MultipartFile file, Model model) {
        try {
            String result = pdfService.processPDF(file);
            model.addAttribute("result", result);
            return "admin/pdf/result";
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi khi xử lý file PDF.");
            return "admin/pdf/upload";
        }
    }
}