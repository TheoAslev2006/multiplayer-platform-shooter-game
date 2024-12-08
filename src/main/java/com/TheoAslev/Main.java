package com.TheoAslev;

import com.TheoAslev.graphics.Game;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    private final static String TITLE = "platform shooter";
    public static int SCREEN_WIDTH = 1280;
    public static int SCREEN_HEIGHT = 960;

    public Main(){
        initStartScreen();
    }

    public void initStartScreen(){
        //StartScreen Frame
        JFrame frame = new JFrame("StartScreen");
        frame.setPreferredSize(new Dimension(700,500));
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        //Creates Start button
        JButton startButton = new JButton("Start Game");
        startButton.setBackground(Color.GRAY);
        startButton.setSize(300,75);
        startButton.setLocation(50, 30);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initGame();
                frame.setVisible(false);
            }
        });

        //adds the screen objects and shows the initScreen
        frame.add(startButton);
        frame.pack();
        frame.setVisible(true);
    }

    public void initGame(){
        JFrame frame = new JFrame(TITLE);
        Game game = new Game(SCREEN_WIDTH, SCREEN_HEIGHT);
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
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}