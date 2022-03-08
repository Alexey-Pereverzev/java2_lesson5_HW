package com.example.java2_lesson5_hw;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class HelloController {

    @FXML
    private TextField messageField;

    @FXML
    private ListView<String> messagesList;

    @FXML
    private TextArea namesField;


    @FXML
    private MenuItem closeField;

    @FXML
    private MenuItem clearChatArea;

    @FXML
    private MenuItem aboutButton;

    @FXML
    public void sendMessage() {
        String message = messageField.getText().trim();
        if (message.length() != 0) {
            messagesList.getItems().add(0, message);
        }
        messageField.setText("");
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
        messagesList.getItems().clear();
    }

    @FXML
    void throwAboutInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(null);
        alert.setContentText("""
                Добро пожаловать в приложение Alex Chat!
                Версия 1.0
                Дата релиза 23.02.2022
                Автор: Переверзев Алексей""");
        alert.showAndWait();
    }

    @FXML
    void initialize() {
        namesField.setText("""
                Диана
                Тимофей
                Андрей
                """);
    }

}