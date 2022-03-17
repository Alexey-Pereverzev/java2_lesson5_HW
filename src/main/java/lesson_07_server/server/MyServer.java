package lesson_07_server.server;

import lesson_07_server.server.authentication.AuthenticationService;
import lesson_07_server.server.authentication.BaseAuthenticationService;
import lesson_07_server.server.handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {
    private final ServerSocket serverSocket;
    private final AuthenticationService authenticationService;
    private final List<ClientHandler> clients;

    public MyServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        authenticationService = new BaseAuthenticationService();
        clients = new ArrayList<>();
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public void start() {
        System.out.println("СЕРВЕР ЗАПУЩЕН!");
        System.out.println("---------------");

        try {
            while (true) {
                waitAndProcessNewClientConnection();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void waitAndProcessNewClientConnection() throws IOException {
        System.out.println("Ожидание подключения...");
        Socket socket = serverSocket.accept();
        System.out.println("Клиент подключился");
        processClientConnection(socket);
    }

    private void processClientConnection(Socket socket) throws IOException {
        ClientHandler handler = new ClientHandler(this, socket);
        handler.handle();
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public synchronized void unSubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public synchronized boolean isUserOnline(String username) {
        for (ClientHandler client : clients) {
            if (username.equals(client.getUsername())) {
                return true;
            }
        }
        return false;
    }

    public synchronized void broadcastMessage(String message, ClientHandler sender) throws IOException {
        for (ClientHandler client : clients) {
            if (!client.equals(sender)) {
                client.sendMessage(sender.getUsername(),message);
            }
        }
    }

    public void sendPrivateMsg(String privateMsg, String addressee, String sender) throws IOException {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(addressee)) {
                client.sendPrivateMessage(sender, privateMsg);
            }
        }
    }
}
