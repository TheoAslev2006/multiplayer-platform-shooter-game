package com.TheoAslev.main.graphics;

import com.TheoAslev.main.eventListeners.KeyControls;
import com.TheoAslev.main.eventListeners.MouseControls;

import javax.swing.*;
import java.awt.*;

public class Game extends JPanel implements Runnable{
	final int screenWidth;
    final int screenHeight;
    int maxFps = (1000 / 59);
    Thread thread;
    int currentFrames;
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
    }

    public void start(){
        if (thread == null){
            thread = new Thread(this);
            thread.start();
        }
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
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

    }
}
