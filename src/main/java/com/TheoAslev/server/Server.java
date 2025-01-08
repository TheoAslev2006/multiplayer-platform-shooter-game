package com.TheoAslev.server;

import com.TheoAslev.graphics.Game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable {
    ServerSocket serverSocket;
    Game game;

    public Server(Game game) {
        //initializes a new server socket for the server to connect to sockets with
        this.game = game;
        try {
            serverSocket = new ServerSocket(5564);
            serverSocket.setReuseAddress(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        //searches after new clients to bond to with the client process
        while (true) {
            Socket client = null;
            try {
                client = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("could not find client");
                continue;
            }
            System.out.println("Client has been accepted");
            new Thread(new ClientProcess(client, this)).start();
        }
    }
}
