package com.TheoAslev.server;

import com.TheoAslev.graphics.Game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//server sided class that connects client to server that also uses the client process to send and receive information
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
        //searches after client and then create a client process that handles information that is send from the server to the client and vice versa
        Socket client = null;
        try {
            client = serverSocket.accept();
        } catch (IOException e) {
            System.out.println("could not find client");
        }
        System.out.println("Client has been accepted");
        new Thread(new ClientProcess(client, this)).start();
    }

}
