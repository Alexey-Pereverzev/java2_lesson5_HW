package com.example.java2_lesson5_hw.server.models;

import lombok.Data;

@Data
public class User {
    private final String login;
    private final String password;
    private final String username;
}
