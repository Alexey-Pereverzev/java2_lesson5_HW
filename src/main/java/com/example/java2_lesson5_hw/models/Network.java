package com.example.java2_lesson5_hw.models;

import com.example.java2_lesson5_hw.controllers.ClientController;
import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Network {

    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + password
    private static final String AUTH_OK_PREFIX = "/auth_ok"; // + username
    private static final String AUTH_ERR_PREFIX = "/auth_err"; // + error message
    private static final String CLIENT_MSG_PREFIX = "/cm"; // + msg
    private static final String SERVER_MSG_PREFIX = "/sm"; // + msg
    private static final String PRIVATE_MSG_PREFIX = "/pm"; // + client + msg
    private static final String STOP_SERVER_CMD_PREFIX = "/stop";
    private static final String END_CLIENT_CMD_PREFIX = "/end"; //
    public static final String CLOSE_CLIENT_CMD_PREFIX = "/close"; //
    private static final String CLIENT_ADD_PREFIX = "/cl_add"; // + кто подключился   !!!!!!!
    private static final String CLIENT_REMOVE_PREFIX = "/cl_rmv"; // + кто отключился   !!!!!!!
    private static final String USER_LIST_REQUEST = "/ul_req";  // - запрос списка пользователей !!!!
    private static final String USER_LIST_ANSWER = "/ul_answ";  // - список пользователей !!!!

    public static final String LOCAL_HOST = "localhost";
    public static final int DEFAULT_PORT = 8186;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;
    private final String host;
    private final int port;
    private List<String> usersOnline;


    public List<String> getUsersOnline() {
        return usersOnline;
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



    public void waitMessage (ClientController clientController) {
        Thread t = new Thread(() -> {
            try {
                String message;
                while (true) {
                    message = in.readUTF();
                    if (message!=null) {
                        if (message.startsWith(CLIENT_MSG_PREFIX)) {
                            String[] parts = message.split("\\s+", 3);
                            String sender = parts[1];
                            String clientMessage = parts[2];
                            Platform.runLater(() -> clientController.appendMessage(String.format("%s: %s", sender, clientMessage)));
                        } else if (message.startsWith(SERVER_MSG_PREFIX)) {
                            String[] parts = message.split("\\s+", 2);
                            String serverMessage = parts[1];
                            Platform.runLater(() -> clientController.appendServerMessage(serverMessage));
                        } else if (message.startsWith(CLIENT_REMOVE_PREFIX)) {              //!!!!!!!!
                            String userDeleted = message.split("\\s+", 2)[1];
                            if (!username.equals(userDeleted)) {
                                deleteUserFromList(userDeleted, clientController);
                            } else {
                                return;
                            }
                        } else if (message.startsWith(CLIENT_ADD_PREFIX)) {
                            String userAdded = message.split("\\s+", 2)[1];
                            addUserToList(clientController, userAdded);
                        } else if (message.startsWith(USER_LIST_ANSWER)) {
                            String[] parts = message.split("\\s+");
                            List<String> users = new ArrayList<>(20);
                            users.addAll(Arrays.asList(parts).subList(1, parts.length));
                            usersOnline = users;
                        } else if (message.startsWith(PRIVATE_MSG_PREFIX)) {
                            String[] parts = message.split("\\s+", 3);
                            String privateMessage = "(".concat(parts[1]).concat("): ").concat(parts[2]);
                            clientController.appendMessage("private " + privateMessage);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
    }


    public void addUserToList(ClientController clientController, String userAdded) {
        if (!username.equals(userAdded)) {
            clientController.getNamesField().getItems().add(userAdded);
            clientController.getNamesField().refresh();
        }
    }

    private void deleteUserFromList(String userDeleted, ClientController clientController) {
        clientController.getNamesField().getItems().remove(userDeleted);
        clientController.getNamesField().refresh();
    }

    public String sendAuthMessage(String login, String password) {
        try {
            out.writeUTF(String.format("%s %s %s", AUTH_CMD_PREFIX, login, password));
            Thread.sleep(100);
            String response = in.readUTF();
            if (response.startsWith(AUTH_OK_PREFIX)) {
                this.username = response.split("\\s+", 2)[1];
                return null;
            } else {
                return response.split("\\s+", 2)[1];
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }


    public String getUsername() {
        return username;
    }


    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при отправке сообщения");
        }
    }

    public void sendPrivateMessage(String selectedRecipient, String message) {
        sendMessage(String.format("%s %s %s", PRIVATE_MSG_PREFIX, selectedRecipient, message));
    }

    public void sendUserListRequest() throws IOException {
        out.writeUTF(USER_LIST_REQUEST);
    }

}
