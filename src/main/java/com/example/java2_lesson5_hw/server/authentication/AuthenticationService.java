package com.example.java2_lesson5_hw.server.authentication;

public interface AuthenticationService {

    String getUsernameByLoginAndPassword(String login, String password);
    void startAuthentication();
    void endAuthentication();
}
