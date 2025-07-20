package com.quizletclone.flashcard.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public class ImageHelper {
    private static final String BASE_UPLOAD_DIR = "flashcard/uploads";

    public static String saveImage(MultipartFile imageFile, String folder) throws IOException {
        Path folderPath = Paths.get(BASE_UPLOAD_DIR, folder).toAbsolutePath();
        Files.createDirectories(folderPath);

        String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        Path destination = folderPath.resolve(filename);

        imageFile.transferTo(destination.toFile());

        return "/images/" + filename;
    }

    public static String downloadImageFromUrl(String imageUrl, String folder) throws IOException {
        Path folderPath = Paths.get(BASE_UPLOAD_DIR, folder).toAbsolutePath();
        Files.createDirectories(folderPath);

        String filename = UUID.randomUUID() + ".jpg";
        Path destination = folderPath.resolve(filename);

        try (InputStream in = new URL(imageUrl).openStream()) {
            Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
        }

        return "/images/" + filename;
    }

    // Optional: Xóa ảnh cũ nếu cần
    public static void deleteImage(String imageUrl, String folder) throws IOException {
        if (imageUrl == null || imageUrl.isEmpty())
            return;

        String filename = Paths.get(imageUrl).getFileName().toString();
        Path filePath = Paths.get(BASE_UPLOAD_DIR, folder, filename).toAbsolutePath();
        Files.deleteIfExists(filePath);
    }
}
