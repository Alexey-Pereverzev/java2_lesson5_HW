package com.example.java2_lesson5_hw;

import com.example.java2_lesson5_hw.controllers.AuthController;
import com.example.java2_lesson5_hw.controllers.ClientController;
import com.example.java2_lesson5_hw.models.Network;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ClientChatApplication extends Application {

    public Network network;
    private Stage primaryStage;
    private Stage authStage;
    private ClientController clientController;


    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        network = new Network();
        network.connect();
        authAndChat();
    }

    public void authAndChat() throws IOException {
        openAuthDialogue();
        createChatDialogue();
    }

    public void openAuthDialogue() throws IOException {
        primaryStage.close();
        FXMLLoader authLoader = new FXMLLoader(ClientChatApplication.class.getResource("auth-view.fxml"));
        authStage = new Stage();
        Scene scene = new Scene(authLoader.load());
        authStage.setScene(scene);
        authStage.initModality(Modality.WINDOW_MODAL);
        authStage.initOwner(primaryStage);
        authStage.setTitle("Authentication ");
        authStage.setX(100);
        authStage.setY(100);
        authStage.setAlwaysOnTop(true);
        authStage.show();
        AuthController authController = authLoader.getController();
        authController.setNetwork(network);
        authController.setStartClient(this);
    }

    private void createChatDialogue() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientChatApplication.class.getResource("client-chat-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setX(100);
        primaryStage.setY(100);
        primaryStage.setAlwaysOnTop(true);
        clientController = fxmlLoader.getController();
        clientController.setNetwork(network);
        clientController.setChatApplication(this);
        primaryStage.setOnCloseRequest((EventHandler) event -> {
            network.sendMessage(Network.CLOSE_CLIENT_CMD_PREFIX);
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch();
    }

    public void openChatDialogue() throws IOException, InterruptedException {
        authStage.close();
        primaryStage.show();
        primaryStage.setTitle("Alex Chat 1.4 - " + network.getUsername());
        network.waitMessage(clientController);
        network.sendUserListRequest();
        Thread.sleep(100);
        clientController.setUsernameTitle(network.getUsername());
        List<String> usersOnline = network.getUsersOnline();
        for (String s : usersOnline) {
            if (s.equals(network.getUsername())) {
                s = ">>> ".concat(s);
            }
            clientController.getNamesField().getItems().add(s);
            clientController.getNamesField().refresh();
        }
    }

    public void showErrorAlert(String title, String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(errorMessage);
        alert.show();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}