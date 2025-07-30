package com.quizletclone.flashcard.dto;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;
    private String otp;
    private String email;
}
