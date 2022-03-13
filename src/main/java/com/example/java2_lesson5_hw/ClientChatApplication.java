package com.example.java2_lesson5_hw;

import com.example.java2_lesson5_hw.controllers.ClientController;
import com.example.java2_lesson5_hw.models.Network;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientChatApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientChatApplication.class.getResource("client-chat-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Alex Chat 1.0");
        stage.setScene(scene);
        stage.setX(100);
        stage.setY(100);
        stage.setAlwaysOnTop(true);
        stage.show();

        Network network = new Network();
        ClientController clientController = fxmlLoader.getController();

        clientController.setNetwork(network);

        network.connect();
        network.waitMessage(clientController);
    }

    public static void main(String[] args) {
        launch();
    }
}