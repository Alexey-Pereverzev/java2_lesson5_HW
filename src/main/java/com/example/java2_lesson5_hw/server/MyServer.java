package com.example.java2_lesson5_hw.server;

import com.example.java2_lesson5_hw.server.authentication.AuthenticationService;
import com.example.java2_lesson5_hw.server.authentication.BaseAuthenticationService;
import com.example.java2_lesson5_hw.server.handler.ClientHandler;

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

    public synchronized void unSubscribe(ClientHandler clientHandler) throws IOException {
        ClientHandler handler = clientHandler;
        clients.remove(clientHandler);
        handler.handle();
    }

    public synchronized void unSubscribeAndTerminate(ClientHandler clientHandler) {
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

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public synchronized void broadcastMessage(String message, ClientHandler sender, boolean isServerMessage) throws IOException {
        for (ClientHandler client : clients) {
            if (!client.equals(sender)) {
                client.sendMessage(isServerMessage ? null : sender.getUsername(), message);
            }
        }
    }

    public synchronized void broadcastMessage(String message, ClientHandler sender) throws IOException {
        broadcastMessage(message, sender, false);
    }

    public synchronized void broadcastAddedUser(ClientHandler sender) throws IOException {
        for (ClientHandler client : clients) {
            if (!client.equals(sender)) {
                client.sendAddUserMessage(sender.getUsername());
            }
        }
    }

    public synchronized void broadcastDeletedUser(ClientHandler sender) throws IOException {
        for (ClientHandler client : clients) {
            if (!client.equals(sender)) {
                client.sendDeleteUserMessage(sender.getUsername());
            }
        }
    }

    public synchronized void sendPrivateMsg(String privateMsg, String sender, String recipient) throws IOException {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(recipient)) {
                client.sendPrivateMessage(sender, privateMsg);
            }
        }
    }

    public List<ClientHandler> getClients() {
        return clients;
    }
}
