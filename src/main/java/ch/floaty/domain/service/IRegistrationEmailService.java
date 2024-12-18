package ch.floaty.domain.service;

import ch.floaty.domain.model.EmailVerificationToken;
import ch.floaty.domain.model.User;

public interface IRegistrationEmailService {
    public void sendRegistrationEmail(User user, EmailVerificationToken token);
}
