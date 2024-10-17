package com.TheoAslev.main;

import com.TheoAslev.main.graphics.Game;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    private final static String TITLE = "platform shooter";
    public final static int SCREEN_WIDTH = 1600;
    public final static int SCREEN_HEIGHT = 900;
    public static void main(String[] args) {
        //initialization of window
        JFrame frame = new JFrame(TITLE);
        Game game = new Game(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //saves data on windows close
        frame.addWindowListener(new WindowAdapter(){
        	@Override
        	public void windowClosing(WindowEvent e) {
                //TODO add serialization to closing event
        		System.exit(0);
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
    }
}