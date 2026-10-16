
package com.ga.waslah.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MockEmailService {

    public void sendVerificationEmail(
            String email,
            String verificationLink
    ) {
        log.info(
                "MOCK EMAIL [verification] to={} link={}",
                email,
                verificationLink
        );
    }

    public void sendPasswordResetEmail(
            String email,
            String resetLink
    ) {
        log.info(
                "MOCK EMAIL [password-reset] to={} link={}",
                email,
                resetLink
        );
    }
}
