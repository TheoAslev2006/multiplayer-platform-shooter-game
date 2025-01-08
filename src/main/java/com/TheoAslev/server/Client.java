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
import java.util.Scanner;

public class Client implements Serializable {
    public Client(Game game, String ip) {
        //initializes a client process from client perspective
        Socket socket;
        while (true)
            try {
                socket = new Socket(ip, 5564);
                break;
            } catch (IOException e) {
                System.err.println("no connection found");
                Scanner scanner = new Scanner(System.in);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                System.out.print("enter other ip ->");
                ip = scanner.nextLine();
            }
        new Thread(new ClientProcess(socket, game)).start();
    }
}
