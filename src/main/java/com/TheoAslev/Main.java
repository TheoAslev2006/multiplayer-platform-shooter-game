package com.TheoAslev;

import com.TheoAslev.graphics.Game;
import com.TheoAslev.level.Level;
import com.TheoAslev.level.Levels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class Main {
    private final static String TITLE = "platform shooter";
    public final static int SCREEN_WIDTH = 1280;
    public final static int SCREEN_HEIGHT = 960;
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
    }
}