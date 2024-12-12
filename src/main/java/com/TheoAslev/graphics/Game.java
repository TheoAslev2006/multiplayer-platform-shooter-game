package com.TheoAslev.graphics;

import com.TheoAslev.character.Player;
import com.TheoAslev.Main;
import com.TheoAslev.eventListeners.KeyControls;
import com.TheoAslev.eventListeners.MouseControls;
import com.TheoAslev.level.Level;
import com.TheoAslev.level.Levels;
import com.TheoAslev.objects.Bullet;
import com.TheoAslev.server.Client;
import com.TheoAslev.server.ClientProcess;
import com.TheoAslev.server.Server;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class Game extends JPanel implements Runnable{
	final int screenWidth;
    final int screenHeight;
    final boolean isHosting;
    int maxFps = (1000 / 60);
    int currentFrames;
    public long threadRunTime;
    public Queue<JSONObject> serverQueue;
    Server server;
    Client client;
    Thread thread;
    public Level level;
    KeyControls keyControls;
    MouseControls mouseControls;
    Point mouseLocation;
    ArrayList<Bullet> bullets = new ArrayList<>();
    public ArrayList<Player> players = new ArrayList<>();
    public Game(int screenWidth, int screenHeight, boolean isHosting){
        //initialization of JPanel
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.isHosting = isHosting;
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
            players.add(new Player( this)) ;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (isHosting) {
            server = new Server(this);
            System.out.println("server created");
        }else {
            client = new Client(this);
        }
        serverQueue = new LinkedList<>();
    }

    public void start(){
        //starts thread
        if (thread == null){
            thread = new Thread(this);
            thread.start();
            if (server!=null)
                new Thread(server).start();
        }
    }

    public void serverUpdate(){
        for (int i = 0; i < serverQueue.size(); i++) {
            parseJson(serverQueue.poll());
        }
    }

    public void parseJson(JSONObject jsonPackage){
        if (jsonPackage.has("players")){
            JSONArray playerData = jsonPackage.getJSONArray("players");
            if (playerData.length() % 2 != 0) throw new RuntimeException();
            for (int i = 0; i < playerData.length(); i+=2) {
                int id = playerData.getInt(i);
                String[] cords = playerData.getString(i + 1).split(",");
                int x = Integer.parseInt(cords[0]);
                int y = Integer.parseInt(cords[1]);

                if (id < 0 || id >= players.size()){
                    System.err.println("player size and id mismatch error");
                }
                players.get(id).setX(x);
                players.get(id).setY(y);
                System.out.println("playersize: " + players.size());
            }
        }
    }

    public void update(){

        serverUpdate();
        //updates the character location upon movement
        Player player = players.getFirst();
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
        if (threadRunTime % 400 == 0){
            System.out.println(players.toString());
            System.out.println(players.size());
            System.out.println(serverQueue.size());

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
        if (!players.isEmpty())
            for (int i = 0; i < players.size(); i++) {
                players.get(i).renderPlayer(g2d);
            }
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
            //System.out.println("updated");
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
