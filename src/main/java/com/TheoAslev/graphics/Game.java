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
import java.util.*;

public class Game extends JPanel implements Runnable {
    final int screenWidth;
    final int screenHeight;
    public final boolean isHosting;
    int maxFps = (1000 / 60);
    int currentFrames;
    public long threadRunTime;
    public String name;
    public Queue<JSONObject> serverQueue;
    Server server;
    Client client;
    Thread thread;
    public Level level;
    KeyControls keyControls;
    MouseControls mouseControls;
    Point mouseLocation;
    public HashMap<String, ArrayList<Bullet>> bullets = new HashMap<>();
    public HashMap<String, Player> players = new HashMap<>();

    public Game(int screenWidth, int screenHeight, boolean isHosting, String name) {
        //initialization of JPanel
        this.name = name;
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
            players.put(name, new Player(this, name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (isHosting) {
            server = new Server(this);
            System.out.println("server created");
        } else {
            client = new Client(this);
        }
        serverQueue = new LinkedList<>();
    }

    public void start() {
        //starts thread
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
            if (server != null)
                new Thread(server).start();
        }
    }

    public void serverUpdate() {
        for (int i = 0; i < serverQueue.size(); i++) {
            JSONObject jsonPackage = serverQueue.poll();
            if (jsonPackage != null) {
                parseJson(jsonPackage);
            }
        }
    }

    public void parseJson(JSONObject jsonPackage) {
        if (jsonPackage.has("players")) {
            JSONArray playerData = jsonPackage.getJSONArray("players");
            System.out.println(jsonPackage);
            if (playerData.length() % 2 != 0) throw new RuntimeException();
            for (int i = 0; i < playerData.length(); i += 2) {
                String name = playerData.get(i).toString();
                if (!Objects.equals(name, this.name)) {
                    String[] cords = playerData.getString(i + 1).split(",");
                    int x = Integer.parseInt(cords[0]);
                    int y = Integer.parseInt(cords[1]);
                    if (players.get(name) != null) {
                        players.get(name).setX(x);
                        players.get(name).setY(y);
                    } else
                        players.put(name, new Player(this, name));

                }
            }
        } else throw new RuntimeException();
    }

    public void update() {
        serverUpdate();
        //updates the character location upon movement
        Player player = players.get(name);
        player.fall();
        player.tick(threadRunTime);
        player.updatePlayer();
        //Player walks left
        if (keyControls.left) {
            player.moveLeft(3);
        }
        //Player walks right
        if (keyControls.right) {
            player.moveRight(3);
        }
        //Player Jumps
        if (keyControls.jump) {
            player.jump();
        }
        //Player shoots
        if (mouseControls.inWindow) {
            if (mouseControls.shoot) {
                int dx = mouseLocation.x - player.getX();
                int dy = mouseLocation.y - player.getY();
                bullets.get(name).add(player.shoot(Math.toDegrees(Math.atan2(dy, dx))));
                mouseControls.shoot = false;
            }
        }
        for (int i = 0; i < bullets.get(name).size(); i++) {
            bullets.get(name).get(i).move(threadRunTime);
            if (bullets.get(name).get(i).x <= 0 || bullets.get(name).get(i).y <= 0 || bullets.get(name).get(i).x >= Main.SCREEN_WIDTH || bullets.get(name).get(i).y >= Main.SCREEN_HEIGHT) {
                bullets.get(name).remove(bullets.get(name).get(i));
            }
        }
        if (threadRunTime % 400 == 0) {
            System.out.println(players.toString());
            System.out.println(players.size());
            System.out.println(serverQueue.size());
        }
    }

    public void printFpsOnScreen(Graphics2D g2d) {
        //draws fps on screen
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.setColor(Color.BLACK);
        g2d.drawString(currentFrames + " Frames Per Seconds", 10, 10);

    }

    public void paintComponent(Graphics g) {
        //renders all objects
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        printFpsOnScreen(g2d);
        level.render(g2d);
        if (!players.isEmpty())
            players.forEach((key, player) -> {
                player.renderPlayer(g2d);
            });
        bullets.forEach((key, bullets) -> {
            bullets.forEach((bullet) -> {
                bullet.render(g2d);
            });
        });

    }

    @Override
    public void run() {
        //game loop
        //this fps system is not mine
        int frames = 0;
        double unProcessedSeconds = 0;
        long previousTime = System.nanoTime();
        double secondsPerTick = 1 / (double) maxFps;
        long fpsTimer = System.currentTimeMillis();
        while (thread != null) {

            long currentTime = System.nanoTime();
            long passedTime = currentTime - previousTime;
            previousTime = currentTime;
            unProcessedSeconds += passedTime / 1_000_000_000.0f;

            while (unProcessedSeconds > secondsPerTick) {
                unProcessedSeconds -= secondsPerTick;

            }
            update();
            //System.out.println("updated");
            repaint();
            frames++;
            threadRunTime += 1;
            if (System.currentTimeMillis() - fpsTimer >= 1000) {
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
