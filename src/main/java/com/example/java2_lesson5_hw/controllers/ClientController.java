package com.example.java2_lesson5_hw.controllers;

import com.example.java2_lesson5_hw.ClientChatApplication;
import com.example.java2_lesson5_hw.models.Network;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

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
    private Label usernameTitle;

    @FXML
    private Button sendButton;

    private String selectedRecipient;

    private Network network;

    public ListView<String> getNamesField() {
        return namesField;
    }

    private ClientChatApplication clientChatApplication;

    @FXML
    void initialize() {
        namesField.setItems(FXCollections.observableArrayList());
        sendButton.setOnAction(event -> sendMessage());
        messageField.setOnAction(event -> sendMessage());
        namesField.setCellFactory(lv -> {
            MultipleSelectionModel<String> selectionModel = namesField.getSelectionModel();
            ListCell<String> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                namesField.requestFocus();
                if (!cell.isEmpty()) {
                    int index = cell.getIndex();
                    if (selectionModel.getSelectedIndices().contains(index)) {
                        selectionModel.clearSelection(index);
                        selectedRecipient = null;
                    } else {
                        selectionModel.select(index);
                        selectedRecipient = cell.getItem();
                    }
                    event.consume();
                }
            });
            return cell;
        });
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    @FXML
    public void sendMessage() {
        String message = messageField.getText().trim();
        messageField.setText("");
        if (message.length() != 0) {
            if (message.startsWith("/end")) {
                network.sendMessage(message);
                try {
                    clientChatApplication.authAndChat();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (selectedRecipient != null) {
                    network.sendPrivateMessage(selectedRecipient, message);
                    appendMessage("Я: (" + selectedRecipient + ") " + message);
                } else {
                    network.sendMessage(message);
                    appendMessage("Я: " + message);
                }
            }
        }
    }

    public void appendMessage(String message) {
        String timeStamp = DateFormat.getInstance().format(new Date());
        messagesList.appendText(timeStamp);
        messagesList.appendText(System.lineSeparator());
        messagesList.appendText(message);
        messagesList.appendText(System.lineSeparator());
        messagesList.appendText(System.lineSeparator());
    }

    @FXML
    public void checkEnter(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            sendMessage();
        }
    }

    @FXML
    void closeApp() {
        network.sendMessage(Network.CLOSE_CLIENT_CMD_PREFIX);
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
                Версия 1.3
                Дата релиза 29.03.2022
                Автор: Переверзев Алексей""");
        alert.showAndWait();
    }


    public void appendServerMessage(String serverMessage) {
        messagesList.appendText(serverMessage);
        messagesList.appendText(System.lineSeparator());
        messagesList.appendText(System.lineSeparator());
    }


    public void setUsernameTitle(String username) {
        this.usernameTitle.setText(username);
    }


    public void setChatApplication(ClientChatApplication clientChatApplication) {
        this.clientChatApplication = clientChatApplication;
    }

}