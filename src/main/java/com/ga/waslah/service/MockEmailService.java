
package com.ga.waslah.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;


@Slf4j
@Profile("dev")
public class MockEmailService implements EmailService{

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
