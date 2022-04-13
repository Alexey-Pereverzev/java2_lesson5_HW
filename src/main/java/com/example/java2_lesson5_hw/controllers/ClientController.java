package com.example.java2_lesson5_hw.controllers;

import com.example.java2_lesson5_hw.ClientChatApplication;
import com.example.java2_lesson5_hw.models.Network;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.io.*;
import java.text.DateFormat;
import java.util.*;

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

    private ArrayList<String> chatHistory;
    private File historyFile;

    public ListView<String> getNamesField() {
        return namesField;
    }

    private ClientChatApplication clientChatApplication;

    @FXML
    void initialize() {
        historyFile = new File("src/main/resources/com/example/java2_lesson5_hw/chat-history.txt");
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
                    String timeStamp = DateFormat.getInstance().format(new Date());
                    appendToChatHistory(timeStamp, network.getUsername().concat(": ").concat(message));
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

    public void appendToChatHistory(String timeStamp, String message) {
        chatHistory.add(timeStamp);
        chatHistory.add(message);
        chatHistory.add(" ");
        trimHistory();
        try {
            writeHistory();
        } catch (IOException e) {
            e.printStackTrace();
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
                Версия 1.5
                Дата релиза 13.04.2022
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

    public ClientChatApplication getClientChatApplication() {
        return clientChatApplication;
    }

    public void readHistory() {
        try (FileReader reader = new FileReader(historyFile)) {
            int c;
            StringBuilder sb = new StringBuilder();
            while ((c = reader.read()) != -1) {
                sb.append((char) c);
            }
            chatHistory = new ArrayList<>(Arrays.asList(sb.toString().split("\n")));
            trimHistory();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String s : chatHistory) {
            messagesList.appendText(replaceMyName(network.getUsername(),s));
            messagesList.appendText(System.lineSeparator());
        }

    }

    private String replaceMyName(String myUsername, String s) {
        if (s.startsWith(myUsername.concat(": "))) {
            String s1 = s.replaceFirst(myUsername, "Я");
            return s1;
        } else return s;
    }

    private void trimHistory() {
        int size = chatHistory.size();
        if (size > 100) {
            for (int i = 0; i < size-100; i++) {
                chatHistory.remove(0);
            }
        }
    }

    public void writeHistory() throws IOException {
        FileWriter writer = new FileWriter(historyFile);
        for (String s : chatHistory) {
            writer.write(s);
            writer.write(System.lineSeparator());
        }
        writer.close();
    }
}