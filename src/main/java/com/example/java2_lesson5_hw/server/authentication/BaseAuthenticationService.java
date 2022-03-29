package com.example.java2_lesson5_hw.server.authentication;

import com.example.java2_lesson5_hw.server.models.User;

import java.util.List;

public class BaseAuthenticationService implements AuthenticationService {

    private static final List<User> clients = List.of(
            new User("user1", "1111", "Диана"),
            new User("user2", "2222", "Тимофей"),
            new User("user3", "3333", "Андрей"),
            new User("user4", "4444", "Дмитрий"),
            new User("user5", "5555", "Арман")
    );

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        for (User client : clients) {
            if (client.getLogin().equals(login) && client.getPassword().equals(password)) {
                return client.getUsername();
            }
        }
        return null;
    }

    @Override
    public void startAuthentication() {
        System.out.println("Старт аутентификации");
    }

    @Override
    public void endAuthentication() {
        System.out.println("Конец аутентификации");
    }
}
