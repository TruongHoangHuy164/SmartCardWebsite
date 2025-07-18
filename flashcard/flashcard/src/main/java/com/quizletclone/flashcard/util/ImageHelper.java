package com.quizletclone.flashcard.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public class ImageHelper {

    // Lưu ảnh vào thư mục ngang src: uploads/upload/
    public static String saveImage(MultipartFile imageFile, String folderName) throws IOException {
        String rootPath = Paths.get("flashcard/uploads", folderName).toAbsolutePath().toString();

        File dir = new File(rootPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = UUID.randomUUID() + "-" + imageFile.getOriginalFilename();
        File dest = new File(dir, fileName);
        imageFile.transferTo(dest);

        // Trả về URL tương đối để truy cập ảnh
        return "/images/" + fileName;
    }

    // Xóa ảnh đã lưu (không log ra console)
    public static boolean deleteImage(String imageUrl, String folderName) {
        if (imageUrl == null || imageUrl.isEmpty())
            return false;

        String fileName = Paths.get(imageUrl).getFileName().toString();
        Path imagePath = Paths.get("flashcard/uploads", folderName, fileName).toAbsolutePath();
        File file = imagePath.toFile();

        return file.exists() && file.delete();
    }
}
