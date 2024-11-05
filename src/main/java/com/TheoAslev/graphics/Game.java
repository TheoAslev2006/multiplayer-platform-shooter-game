package com.TheoAslev.graphics;

import com.TheoAslev.Character.Character;
import com.TheoAslev.Character.Player;
import com.TheoAslev.Main;
import com.TheoAslev.eventListeners.KeyControls;
import com.TheoAslev.eventListeners.MouseControls;
import com.TheoAslev.level.Level;
import com.TheoAslev.level.Levels;
import com.TheoAslev.objects.Bullet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;

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
    Point mouseLocation;
    ArrayList<Bullet> bullets = new ArrayList<>();
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
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                mouseLocation = e.getPoint();
            }
        });
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
        //Player walks left
        if (keyControls.left){
            player.moveLeft(3);
        }
        //Player walks right
        if (keyControls.right){
            player.moveRight(3);
        }
        //Player Jumps
        if (keyControls.jump){
            player.jump();
        }
        //Player shoots
        if (mouseControls.inWindow){
            if (mouseControls.shoot){

                int dx = mouseLocation.x - player.getX() ;
                int dy = mouseLocation.y - player.getY() ;

                bullets.add(player.shoot(Math.toDegrees(Math.atan2(dy, dx)))) ;
                mouseControls.shoot = false;
            }
        }
            for (int i = 0; i < bullets.size(); i++) {
                bullets.get(i).move(threadRunTime);
                if (bullets.get(i).x <= 0 || bullets.get(i).y <= 0 || bullets.get(i).x >= Main.SCREEN_WIDTH || bullets.get(i).y >= Main.SCREEN_HEIGHT) {
                    bullets.remove(bullets.get(i));
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
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).render(g2d);
        }
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
