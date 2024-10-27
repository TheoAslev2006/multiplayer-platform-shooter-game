package com.TheoAslev.StartScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartScreen extends JPanel{
    Thread thread;
    public boolean start;
    public StartScreen() {
        setPreferredSize(new Dimension(500, 500));
        setBackground(Color.white);
        setVisible(true);
        setOpaque(true);
        setDoubleBuffered(true);
        JButton button = new JButton("Start");
        button.setVisible(true);
        button.setBackground(Color.BLACK);
        button.setSize(100,100);
        button.setLocation(200,200);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                start = true;
                System.out.println("Started");
            }
        });
    }
}
