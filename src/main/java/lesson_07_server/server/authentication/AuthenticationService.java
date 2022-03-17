package lesson_07_server.server.authentication;

public interface AuthenticationService {

    String getUsernameByLoginAndPassword(String login, String password);
    void startAuthentication();
    void endAuthentication();
}
