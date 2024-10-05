package ch.floaty.domain;

public interface IAuthenticationService {
    public User register(String username, String email, String password);
    public SessionToken login(String username, String password);
    public void logout();
}
