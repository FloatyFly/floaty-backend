package ch.floaty.domain.service;

import ch.floaty.domain.model.EmailVerificationToken;
import ch.floaty.domain.model.User;
import ch.floaty.infrastructure.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RegistrationEmailService implements IRegistrationEmailService {

    @Value("${app.base.url}")
    private String appBaseUrl;

    private final EmailService emailService;

    public RegistrationEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void sendRegistrationEmail(User user, EmailVerificationToken token) {
        String verificationLink = String.format("%s/auth/verify-email/%s", appBaseUrl, token.getToken());
        String emailText = String.format(
                "Hello %s,\n\n" +
                        "You are almost done with your registration! To validate your email, please click the link below:\n\n" +
                        "%s\n\n" +
                        "If you did not request this registration, please ignore this email.\n\n" +
                        "Thank you,\n" +
                        "The Floaty Team",
                user.getUsername(), verificationLink
        );
        String emailSubject = "Floaty - Complete Your Registration!";

        emailService.sendSimpleEmail(user.getEmail(), emailSubject, emailText);
    }
}
