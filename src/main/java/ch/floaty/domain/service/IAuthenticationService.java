package ch.floaty.domain.service;

import ch.floaty.domain.model.SessionToken;
import ch.floaty.domain.model.User;

public interface IAuthenticationService {
    public User register(String username, String email, String password);
    public SessionToken login(String username, String password);
    public void logout();
}
