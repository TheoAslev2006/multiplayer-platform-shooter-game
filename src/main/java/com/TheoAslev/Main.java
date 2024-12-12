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

    public Main(boolean isHosting){
        System.out.println("Starting...");
        initGame(isHosting);
    }

    public void initStartScreen(){


    }

    public void initGame(boolean isHosting){
        Scanner scanner = new Scanner(System.in);
        boolean mode = scanner.nextBoolean();

        JFrame frame = new JFrame(TITLE);
        Game game = new Game(SCREEN_WIDTH, SCREEN_HEIGHT, mode);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //saves data on windows close
        frame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                //TODO add serialization to closing event
                System.exit(1);
            }
        });
        frame.setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        frame.setResizable(false);
        frame.pack();
        //game JPanel added to frame
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        game.start();
        System.out.println("Game started");
    }

    public static void main(String[] args) {
        new Main(true);
        new Main(false);
    }
}