package com.TheoAslev.server;


import com.TheoAslev.graphics.Game;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

//client side class that connects to server via a client process
public class Client implements Serializable {
    public Client(Game game, String ip) throws Exception {
        //initializes a client process from client perspective
        Socket socket;
        while (true)
            try {
                socket = new Socket(ip, 5564);
                break;
            } catch (IOException e) {
                throw new Exception();
            }
        new Thread(new ClientProcess(socket, game)).start();
    }
}
