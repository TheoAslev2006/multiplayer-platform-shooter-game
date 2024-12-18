package com.TheoAslev.server;


import com.TheoAslev.character.Player;
import com.TheoAslev.graphics.Game;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Client implements Serializable {
    Socket socket;
    public ArrayList<Player> players;
    public ClientProcess clientProcess;
    public Client(Game game){
        try {
            Socket socket = new Socket("127.0.0.1",5564);
            new Thread(new ClientProcess(socket, game)).start();
        }catch (IOException e){
            System.out.println("no connection found");
            throw new RuntimeException();
        }

    }



}
