package com.TheoAslev.graphics;

import com.TheoAslev.Character.Character;
import com.TheoAslev.Character.Player;
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
    Player player;
    KeyControls keyControls;
    MouseControls mouseControls;
    long threadRunTime;
    public Game(int screenWidth, int screenHeight){
        //initialization of JPanel
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        keyControls = new KeyControls();
        mouseControls = new MouseControls();
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setDoubleBuffered(true);
        setOpaque(true);
        setBackground(Color.white);
        setFocusable(true);
        addKeyListener(keyControls);
        addMouseListener(mouseControls);
        try {
            level = new Level(Levels.Level1);
            player = new Player(level.tileMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start(){
        //starts thread
        if (thread == null){
            thread = new Thread(this);
            thread.start();
        }
    }

    public void update(){
        //updates the character location upon movement

        player.fall();
        player.tick(threadRunTime);
        player.updatePlayer();
        if (keyControls.left){
            player.moveLeft(3);
        }
        if (keyControls.right){
            player.moveRight(3);
        }
        if (keyControls.jump){
            player.jump();
        }
        if (mouseControls.inWindow){
            if (mouseControls.shoot){
                player.shoot();
            }
        }
    }

    public void printFpsOnScreen(Graphics2D g2d){
        //draws fps on screen
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.setColor(Color.BLACK);
        g2d.drawString(currentFrames + " Frames Per Seconds", 10, 10 );
    }

    public void paintComponent(Graphics g){
        //renders all objects
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        printFpsOnScreen(g2d);
        level.render(g2d);
        player.renderPlayer(g2d);
    }

    @Override
    public void run() {
        //game loop
        //this fps system is not mine
        int frames = 0;
        double unProcessedSeconds = 0;
        long previousTime = System.nanoTime();
        double secondsPerTick = 1/(double)maxFps;
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
            threadRunTime += 1;
            System.out.println(threadRunTime);
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
