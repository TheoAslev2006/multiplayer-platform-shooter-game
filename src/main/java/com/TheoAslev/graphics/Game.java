package com.TheoAslev.graphics;

import com.TheoAslev.eventListeners.KeyControls;
import com.TheoAslev.eventListeners.MouseControls;
import com.TheoAslev.level.Level;
import com.TheoAslev.level.Levels;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Game extends JPanel implements Runnable{
	final int screenWidth;
    final int screenHeight;
    int maxFps = (1000 / 60);
    Thread thread;
    int currentFrames;
    Level level;
    public Game(int screenWidth, int screenHeight){
        //initialization of JPanel
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        KeyControls keyControls = new KeyControls();
        MouseControls mouseControls = new MouseControls();
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setDoubleBuffered(true);
        setOpaque(true);
        setBackground(Color.white);
        setFocusable(true);
        addKeyListener(keyControls);
        addMouseListener(mouseControls);
        try {
            level = new Level(Levels.Level1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start(){
        if (thread == null){
            thread = new Thread(this);
            thread.start();
        }
    }

    public void update(){

    }

    public void printFpsOnScreen(Graphics2D g2d){
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.setColor(Color.BLACK);
        g2d.drawString(currentFrames + " Frames Per Seconds", 10, 10 );
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        printFpsOnScreen(g2d);
        level.render(g2d);
    }

    @Override
    public void run() {

        int frames = 0;
        double unProcessedSeconds = 0;
        long previousTime = System.nanoTime();
        double secondsPerTick = 1/60.0;
        long fpsTimer = System.currentTimeMillis();

        while (thread != null) {

            long currentTime = System.nanoTime();
            long passedTime = currentTime - previousTime;
            previousTime = currentTime;
            unProcessedSeconds += passedTime/1_000_000_000.0f;

            while (unProcessedSeconds > secondsPerTick){
                unProcessedSeconds-=secondsPerTick;

            }

            update();
            repaint();
            frames++;

            if (System.currentTimeMillis() - fpsTimer >= 1000){
                currentFrames = frames;
                frames = 0;
                fpsTimer += 1000;
            }
            try {
                Thread.sleep(maxFps);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        repaint();

    }
}
