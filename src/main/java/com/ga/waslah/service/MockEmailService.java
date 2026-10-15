package com.ga.waslah.service;

import org.springframework.stereotype.Service;

@Service
public class MockEmailService {

    public void sendVerificationEmail(
            String email,
            String verificationLink
    ) {

        System.out.println();
        System.out.println("========================================");
        System.out.println("       WASLAH EMAIL VERIFICATION");
        System.out.println("========================================");
        System.out.println("To: " + email);
        System.out.println();
        System.out.println("Please verify your WASLAH account:");
        System.out.println();
        System.out.println(verificationLink);
        System.out.println();
        System.out.println("========================================");
        System.out.println();
    }

    public void sendPasswordResetEmail(
            String email,
            String resetLink
    ) {

        System.out.println("========== PASSWORD RESET EMAIL ==========");
        System.out.println("To: " + email);
        System.out.println("Reset your password using this link:");
        System.out.println(resetLink);
        System.out.println("This link will expire in 15 minutes.");
        System.out.println("==========================================");
    }
}

