package lesson_07_server.server.handler;

import lesson_07_server.server.MyServer;
import lesson_07_server.server.authentication.AuthenticationService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {

    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + password
    private static final String AUTH_OK_PREFIX = "/auth_ok"; // + username
    private static final String AUTH_ERR_PREFIX = "/auth_err"; // + error message
    private static final String CLIENT_MSG_PREFIX = "/cMsg"; // + msg
    private static final String SERVER_MSG_PREFIX = "/sMsg"; // + msg
    private static final String PRIVATE_MSG_PREFIX = "/pMsg"; // + msg
    private static final String STOP_SERVER_CMD_PREFIX = "/stop";
    private static final String END_CLIENT_CMD_PREFIX = "/end"; // + login + password

    private MyServer myServer;
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;
    private String username;

    public ClientHandler(MyServer myServer, Socket socket) {
        this.myServer = myServer;
        clientSocket = socket;
    }

    public void handle() throws IOException {
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new DataInputStream(clientSocket.getInputStream());

        new Thread(() -> {
            try {
                authentication();
                chatWithOthers();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
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
                System.out.println("Пользователь " + username + " подключился к чату");
                myServer.subscribe(this);
                return true;
            } else {
                out.writeUTF(AUTH_ERR_PREFIX + " Неверный логин и/или пароль");
                return false;
            }
        }
    }

    public String getUsername() {
        return username;
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
                    System.out.println(username + " отключился от чата");
                    return;
                } else if (message.startsWith(PRIVATE_MSG_PREFIX)) {
                    int space1 = message.indexOf(" ");
                    if (space1 > -1) {
                        String userAndMsg = message.substring(space1+1);
                        int space2 = userAndMsg.indexOf(" ");
                        if (space2 > -1) {
                            String addressee = userAndMsg.substring(0, space2);
                            String privateMsg = userAndMsg.substring(space2+1);
//                            System.out.println("_" + addressee + "_" + privateMsg);
                            myServer.sendPrivateMsg(privateMsg, addressee, username);
                        }
                    }
                } else {
                    myServer.broadcastMessage(message, this);
                }
            }
        }
    }

    public void sendMessage (String sender, String message) throws IOException {
        out.writeUTF(String.format("%s %s %s", CLIENT_MSG_PREFIX, sender, message));
    }

    public void sendPrivateMessage(String sender, String privateMsg) throws IOException {
        out.writeUTF(String.format("%s %s %s", "private:", sender, privateMsg));
    }
}
