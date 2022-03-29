package com.example.java2_lesson5_hw.server.handler;

import com.example.java2_lesson5_hw.server.MyServer;
import com.example.java2_lesson5_hw.server.authentication.AuthenticationService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ClientHandler {

    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + password
    private static final String AUTH_OK_PREFIX = "/auth_ok"; // + username
    private static final String AUTH_ERR_PREFIX = "/auth_err"; // + error message
    private static final String CLIENT_MSG_PREFIX = "/cm"; // + msg
    private static final String SERVER_MSG_PREFIX = "/sm"; // + msg
    private static final String PRIVATE_MSG_PREFIX = "/pm"; // + client + msg
    private static final String STOP_SERVER_CMD_PREFIX = "/stop";
    private static final String END_CLIENT_CMD_PREFIX = "/end"; //
    private static final String CLOSE_CLIENT_CMD_PREFIX = "/close"; //
    private static final String CLIENT_ADD_PREFIX = "/cl_add"; // + кто подключился   !!!!!!!
    private static final String CLIENT_REMOVE_PREFIX = "/cl_rmv"; // + кто отключился   !!!!!!!
    private static final String USER_LIST_REQUEST = "/ul_req";  // - запрос списка пользователей !!!!
    private static final String USER_LIST_ANSWER = "/ul_answ";  // - список пользователей !!!!

    private MyServer myServer;
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;
    private String username;

    public ClientHandler(MyServer myServer, Socket socket) throws IOException {
        this.myServer = myServer;
        clientSocket = socket;
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new DataInputStream(clientSocket.getInputStream());
    }

    public void handle() throws IOException {
        new Thread(() -> {
            try {
                authentication();
                chatWithOthers();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                try {
                    myServer.unSubscribe(this);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try {
                    myServer.broadcastDeletedUser(this);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    private void authentication() throws IOException, InterruptedException {
        String message;
        while (true) {
            message = in.readUTF();
            if (message.startsWith(AUTH_CMD_PREFIX)) {
                boolean successAuth = processAuthentication(message);
                if (successAuth) {
                    break;
                } else {
                    System.out.println("Неудачная попытка аутентификации");
                }
            } else {
                out.writeUTF(AUTH_ERR_PREFIX + " Неверная команда для аутентификации");
                System.out.println("Неудачная попытка аутентификации");
            }
        }
    }

    private boolean processAuthentication(String message) throws IOException {
        String[] parts = message.split("\\s+");
        if (parts.length!=3) {
            out.writeUTF(AUTH_ERR_PREFIX + " Неверный формат строки аутентификации");
            return false;
        } else {
            String login = parts[1];
            String password = parts[2];
            AuthenticationService auth = myServer.getAuthenticationService();
            username = auth.getUsernameByLoginAndPassword(login,password);
            if (username!=null) {
                if (myServer.isUserOnline(username)) {
                    out.writeUTF(AUTH_ERR_PREFIX + " Данный пользователь уже онлайн");
                    return false;
                }
                out.writeUTF(AUTH_OK_PREFIX + " " + username);

                myServer.subscribe(this);
                myServer.broadcastAddedUser(this);
                System.out.println("Пользователь " + username + " подключился к чату");
                myServer.broadcastMessage(String.format(">>> %s присоединился к чату", username), this, true);
                return true;
            } else {
                out.writeUTF(AUTH_ERR_PREFIX + " Неверный логин и/или пароль");
                return false;
            }
        }
    }



    private void chatWithOthers() throws IOException {
        String message;
        while (true) {
            message = in.readUTF();
            if (message!=null) {
                System.out.println("message | " + username + ": " + message);
                if (message.startsWith(STOP_SERVER_CMD_PREFIX)) {
                    System.exit(0);
                } else if (message.startsWith(END_CLIENT_CMD_PREFIX)) {
                    out.writeUTF(CLIENT_REMOVE_PREFIX + " " + username);  //!!!!!!
                    myServer.broadcastMessage(username + " отключился от чата", this, true);
                    System.out.println(username + " отключился от чата");
                    myServer.broadcastDeletedUser(this);
                    myServer.unSubscribe(this);                     //!!!!!!!!
                    return;
                } else if (message.startsWith(PRIVATE_MSG_PREFIX)) {
                    String[] parts = message.split("\\s+", 3);
                    String recipient = parts[1];
                    String privateMessage = parts[2];
                    myServer.sendPrivateMsg(privateMessage, username, recipient);
                } else if (message.startsWith(USER_LIST_REQUEST)) {                 // !!!!!!!
                    List<ClientHandler> clients = myServer.getClients();
                    String users_answer = USER_LIST_ANSWER;
                    for (ClientHandler client : clients) {
                        users_answer = users_answer.concat(" " + client.getUsername());
                    }
                    out.writeUTF(users_answer);
                } else if (message.startsWith(CLOSE_CLIENT_CMD_PREFIX)) {
                    myServer.broadcastMessage(username + " отключился от чата", this, true);
                    System.out.println(username + " отключился от чата");
                    myServer.broadcastDeletedUser(this);
                    myServer.unSubscribeAndTerminate(this);                     //!!!!!!!!
                    return;
                } else {
                    myServer.broadcastMessage(message, this);
                }
            }
        }
    }

    public void sendMessage (String sender, String message) throws IOException {
        if (sender != null) {
            out.writeUTF(String.format("%s %s %s", CLIENT_MSG_PREFIX, sender, message));
        } else {
            out.writeUTF(String.format("%s %s", SERVER_MSG_PREFIX, message));
        }
    }

    public void sendPrivateMessage(String sender, String privateMsg) throws IOException {
        out.writeUTF(String.format("%s %s %s", PRIVATE_MSG_PREFIX, sender, privateMsg));
    }

    public String getUsername() {
        return username;
    }

    public void sendAddUserMessage(String username) throws IOException {
        out.writeUTF(String.format("%s %s", CLIENT_ADD_PREFIX, username));
    }

    public void sendDeleteUserMessage(String username) throws IOException {
        out.writeUTF(String.format("%s %s", CLIENT_REMOVE_PREFIX, username));
    }
}
