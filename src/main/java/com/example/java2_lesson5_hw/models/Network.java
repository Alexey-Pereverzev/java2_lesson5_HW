package com.example.java2_lesson5_hw.models;

import com.example.java2_lesson5_hw.controllers.ClientController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class Network {

    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + password
    private static final String AUTH_OK_PREFIX = "/auth_ok"; // + username
    private static final String AUTH_ERR_PREFIX = "/auth_err"; // + error message
    private static final String CLIENT_MSG_PREFIX = "/cMsg"; // + msg
    private static final String SERVER_MSG_PREFIX = "/sMsg"; // + msg
    private static final String PRIVATE_MSG_PREFIX = "/pMsg"; // + msg
    private static final String STOP_SERVER_CMD_PREFIX = "/stop";
    private static final String END_CLIENT_CMD_PREFIX = "/end"; // + login + password

    public static final String LOCAL_HOST = "localhost";
    public static final int DEFAULT_PORT = 8186;
    private DataInputStream in;
    private DataOutputStream out;

    private final String host;
    private final int port;

    public Network(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Network() {
        this.host = LOCAL_HOST;
        this.port = DEFAULT_PORT;
    }

    public void connect() {
        Socket socket;
        try {
            socket = new Socket(host, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка, соединение не установлено...");
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при отправке сообщения");
        }
    }

    public void waitMessage (ClientController clientController) {
        Thread t = new Thread(() -> {
            try {
                String message;
                while (true) {
                    message = in.readUTF();
                    if (message!=null) {
                        clientController.appendMessage(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
    }
}
