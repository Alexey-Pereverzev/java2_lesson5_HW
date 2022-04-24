package com.example.java2_lesson5_hw.controllers;

import com.example.java2_lesson5_hw.ClientChatApplication;
import com.example.java2_lesson5_hw.models.Network;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class AuthController {
    @FXML
    public TextField loginField;

    @FXML
    public PasswordField passwordField;

    private Network network;
    private ClientChatApplication clientChatApplication;

    @FXML
    public void checkAuth(ActionEvent actionEvent) throws IOException, InterruptedException {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if (login.length() == 0 || password.length() == 0) {
            clientChatApplication.showErrorAlert("Ошибка ввода при аутентификации", "Поля не должны быть пустыми");
            return;
        }

        String authErrorMessage = network.sendAuthMessage(login, password);
        if (authErrorMessage == null) {
            clientChatApplication.openChatDialogue();
        } else {
            clientChatApplication.showErrorAlert("Ошибка аутентификации", authErrorMessage);
        }

    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void setStartClient(ClientChatApplication clientChatApplication) {
        this.clientChatApplication = clientChatApplication;
    }
}

