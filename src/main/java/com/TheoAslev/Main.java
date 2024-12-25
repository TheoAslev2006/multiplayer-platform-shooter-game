package com.TheoAslev;

import com.TheoAslev.graphics.Game;
import com.TheoAslev.server.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Scanner;

public class Main {

    private final static String TITLE = "platform shooter";
    public static int SCREEN_WIDTH = 1280;
    public static int SCREEN_HEIGHT = 960;

    public Main() {

        System.out.println("Starting...");
        initGame();
    }

    public void initStartScreen() {


    }

    public void initGame() {
        Scanner scanner = new Scanner(System.in);
        String name;
        Game game;
        System.out.print("insert ip address to join or enter blank to host");
        String ip = scanner.nextLine();
        if (ip.isEmpty()) {
            name = "server";
            game = new Game(SCREEN_WIDTH, SCREEN_HEIGHT, name);
        } else {
            name = "client";
            System.out.println("ip connected to");
            game = new Game(SCREEN_WIDTH, SCREEN_HEIGHT, name, ip);
        }
        JFrame frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //saves data on windows close
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //TODO add serialization to closing event
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
        game.start();
        System.out.println("Game started");
    }

    public static void main(String[] args) {
        new Main();
    }
}