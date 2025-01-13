package com.TheoAslev;

import com.TheoAslev.graphics.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.Scanner;

//main class where player gets asked to join server via ip or host a server, and it initializes the window as well as the game
public class Main {

    private final static String TITLE = "platform shooter";
    public static int SCREEN_WIDTH = 1280;
    public static int SCREEN_HEIGHT = 960;

    public Main() {
        initGame();
    }

    public void initGame() {
        //asks for ip address
        Scanner scanner = new Scanner(System.in);
        String name = "";
        Game game;
        System.out.print("insert ip address to join or enter blank to host" + "\n" + "--> ip: ");

        while (true) {
            String ip = scanner.nextLine();
            if (ip.isEmpty()) {
                // will initialize the game as a server
                name = "server";
                try {
                    game = new Game(SCREEN_WIDTH, SCREEN_HEIGHT, name);
                } catch (Exception e) {
                    System.err.println("Error initializing game, try again");
                    System.out.print("insert ip address to join or enter blank to host" + "\n" + "--> ip: ");
                    continue;
                }
                break;
            } else {
                // will initialize the game as a client
                name = "client";
                try {
                    game = new Game(SCREEN_WIDTH, SCREEN_HEIGHT, name, ip);
                    break;
                } catch (Exception e) {
                    System.out.print("server is either full or does not exist, try another ip address or host yourself" + "\n" + "--> ip: ");
                    game = null;
                }

            }
        }
        // creates frame and gives it properties
        JFrame frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Closing game");
                System.exit(1);
            }
        });
        frame.setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        frame.setResizable(false);
        //game JPanel added to frame
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        System.out.println("Starting...");
        game.start();
        System.out.println("Game started");


    }

    public static void main(String[] args) {
        //starts the game
        new Main();
    }
}