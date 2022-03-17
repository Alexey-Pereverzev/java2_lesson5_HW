package com.example.java2_lesson5_hw.controllers;

import com.example.java2_lesson5_hw.models.Network;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


public class ClientController {

    @FXML
    private TextField messageField;

    @FXML
    private TextArea messagesList;

    @FXML
    private Label userName;

    @FXML
    private ListView<String> namesField;

    @FXML
    private MenuItem closeField;

    @FXML
    private MenuItem clearChatArea;

    @FXML
    private MenuItem aboutButton;

    @FXML
    public void sendMessage() {
        String message = messageField.getText().trim();
        if (message.length()!=0) {
            network.sendMessage(message);
            appendMessage(message);
        }
        messageField.setText("");
    }

    public void appendMessage(String message) {
        if (message.length()!=0) {
            messagesList.appendText(message);
            messagesList.appendText(System.lineSeparator());
        }
    }

    @FXML
    public void checkEnter(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            sendMessage();
        }
    }

    @FXML
    void closeApp() {
        System.exit(0);
    }

    @FXML
    void clearChat() {
        messagesList.setText("");
    }

    @FXML
    void throwAboutInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(null);
        alert.setContentText("""
                Добро пожаловать в приложение Alex Chat!
                Версия 1.2
                Дата релиза 17.03.2022
                Автор: Переверзев Алексей""");
        alert.showAndWait();
    }

    @FXML
    void initialize() {
        namesField.getItems().add("Диана");
        namesField.getItems().add("Тимофей");
        namesField.getItems().add("Андрей");
        namesField.getItems().add("Дмитрий");
        namesField.getItems().add("Арман");
        userName.setText("Диана");
    }

    private Network network;

    public void setNetwork(Network network) {
        this.network = network;
    }
}