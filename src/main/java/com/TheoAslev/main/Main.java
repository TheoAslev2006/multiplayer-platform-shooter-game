package com.TheoAslev.main;

import com.TheoAslev.main.graphics.Game;
import javax.swing.*;
import java.awt.*;

public class Main {
    private final static String TITLE = "platform shooter";
    public final static int SCREEN_WIDTH = 1600;
    public final static int SCREEN_HEIGHT = 900;
    public static void main(String[] args) {
        //initialization of window
        JFrame frame = new JFrame(TITLE);
        Game game = new Game(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        frame.setResizable(false);
        //game class added to frame
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}