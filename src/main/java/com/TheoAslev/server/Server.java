package com.TheoAslev.server;
import com.TheoAslev.character.Player;
import com.TheoAslev.graphics.Game;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable{
    ServerSocket serverSocket;
    Game game;
    public Server(Game game){
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
        while (true) {
            Socket client = null;
            try {
                client = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("could not find client");
                continue;
            }
            System.out.println("Client has been accepted");
            game.players.add(new Player(game));
            new Thread(new ClientProcess(client, game)).start();
        }
    }
}
