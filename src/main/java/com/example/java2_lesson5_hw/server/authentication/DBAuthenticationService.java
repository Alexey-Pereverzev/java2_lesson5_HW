package com.example.java2_lesson5_hw.server.authentication;

import java.sql.*;

public class DBAuthenticationService implements AuthenticationService {

    private Connection connection;
    private Statement stmt;
    private ResultSet rs;

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) throws SQLException {
        rs = stmt.executeQuery(String.format("SELECT * FROM auth WHERE login = '%s'", login));
        if (rs.isClosed()) {
            return null;
        }
        String username = rs.getString("username");
        String passwordDB = rs.getString("password");

        if (password != null && passwordDB.equals(password)) {
            return username;
        } else {
            return null;
        }
    }

    @Override
    public void startAuthentication() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/db/mainDB.db");
        stmt = connection.createStatement();
    }

    @Override
    public void endAuthentication() throws SQLException {
        connection.close();
    }

    @Override
    public void changeNik(String login, String newName) throws SQLException {
        stmt.executeUpdate(String.format("UPDATE auth SET username = '%s' WHERE login = '%s'", newName, login));
    }

    public boolean isUsernameFree(String usernameForCheck) throws SQLException {
        boolean result = true;
        rs = stmt.executeQuery("SELECT username FROM auth");
        while (rs.next()) {
            if (rs.getString("username").equals(usernameForCheck)) {
                result = false;
            }
        }
        return result;
    }
}
