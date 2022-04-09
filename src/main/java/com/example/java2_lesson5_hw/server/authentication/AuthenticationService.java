package com.example.java2_lesson5_hw.server.authentication;

import java.sql.SQLException;

public interface AuthenticationService {

    String getUsernameByLoginAndPassword(String login, String password) throws SQLException;

    void startAuthentication() throws ClassNotFoundException, SQLException;

    void endAuthentication() throws SQLException;

    void changeNik(String login, String newName) throws SQLException;

    boolean isUsernameFree(String username) throws SQLException;
}
