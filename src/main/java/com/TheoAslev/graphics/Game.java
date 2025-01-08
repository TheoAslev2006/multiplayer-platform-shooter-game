package com.TheoAslev.graphics;

import com.TheoAslev.character.Player;
import com.TheoAslev.Main;
import com.TheoAslev.eventListeners.KeyControls;
import com.TheoAslev.eventListeners.MouseControls;
import com.TheoAslev.level.Level;
import com.TheoAslev.level.Levels;
import com.TheoAslev.objects.Bullet;
import com.TheoAslev.server.Client;
import com.TheoAslev.server.Server;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.*;

public class Game extends JPanel implements Runnable {
    public final int screenWidth;
    public final int screenHeight;
    int maxFps = (1000 / 60);
    int currentFrames;
    public long ticks;
    public String name;
    public Server server;
    public Client client;
    Thread thread;
    public Level level;
    KeyControls keyControls;
    MouseControls mouseControls;
    Point mouseLocation;
    public ArrayList<Bullet> bullets;
    public ArrayList<Bullet> enemyBullets;
    public Queue<String> bulletsFromServer;
    public Queue<String> bulletsToServer;
    public Queue<JSONObject> serverQueue;
    public HashMap<String, Player> players = new HashMap<>();
    public boolean showHitBoxes = true;

    public Game(int screenWidth, int screenHeight, String name) {
        //initialization of JPanel and event listeners
        this.name = name;
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
            players.put(name, new Player(this, name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //initializes queues, arraylists and the server
        server = new Server(this);
        System.out.println("server created");
        bullets = new ArrayList<>();
        enemyBullets = new ArrayList<>();
        serverQueue = new LinkedList<>();
        bulletsFromServer = new LinkedList<>();
        bulletsToServer = new LinkedList<>();
    }

    public Game(int screenWidth, int screenHeight, String name, String ip) {
        //initialization of JPanel
        this.name = name;
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
            players.put(name, new Player(this, name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        client = new Client(this, ip);
        bullets = new ArrayList<>();
        enemyBullets = new ArrayList<>();
        serverQueue = new LinkedList<>();
        bulletsFromServer = new LinkedList<>();
        bulletsToServer = new LinkedList<>();
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
        //takes json from a queue and uses it to give data on other players
        if (jsonPackage.has("players")) {
            JSONArray playerDataInstances = jsonPackage.getJSONArray("players");
            for (int i = 0; i < playerDataInstances.length(); i++) {
                //calculates the coordinates from said player
                JSONObject playerData = playerDataInstances.getJSONObject(i);
                String name = playerData.getString("name");
                String[] coords = playerData.getString("coords").split(",");
                int[] parsedCoords = {Integer.parseInt(coords[0]), Integer.parseInt(coords[1])};
                if (!Objects.equals(name, this.name)) {
                    players.get(name).setX(parsedCoords[0]);
                    players.get(name).setY(parsedCoords[1]);
                }
                //adds new bullets to game from server queue
                JSONArray bullets = playerData.getJSONArray("bullets");
                for (int j = 0; j < bullets.length(); j++) {
                    bulletsFromServer.add(bullets.getString(j));
                }
            }

        } else throw new RuntimeException();
    }

    public void addBullets() {
        // adds bullets to separate arraylist from the queue
        int length = bulletsFromServer.size();
        for (int i = 0; i < length; i++) {
            String bullet = bulletsFromServer.poll();
            assert bullet != null : "Not a valid bullet";
            String[] bulletDataArray = bullet.split(",");
            assert bullet.length() != 3 : "Bullet missing data";
            int[] parsedBulletDataArrayCoordinates = {
                    Integer.parseInt(bulletDataArray[0]), Integer.parseInt(bulletDataArray[1]),
            };
            double radians = Double.parseDouble(bulletDataArray[2]);
            enemyBullets.add(new Bullet(Math.cos(radians) * 100, Math.sin(radians) * 100, radians, new Point(parsedBulletDataArrayCoordinates[0], parsedBulletDataArrayCoordinates[1])));
        }
    }

    public void update() {
        showHitBoxes = keyControls.isHitBoxesVisible;

        //server related updates
        serverUpdate();
        addBullets();

        //updates the character location upon movement
        Player player = players.get(name);
        player.calculatePlayerPosition();
        player.tick(ticks);
        player.updatePlayer();

        //Player walks left
        if (keyControls.left) {
            player.moveLeft(2);
        }

        //Player walks right
        if (keyControls.right) {
            player.moveRight(2);
        }

        //Player Jumps
        if (keyControls.jump) {
            player.jump();
        }

        //Player shoots
        if (mouseControls.shoot) {
            int dx = mouseLocation.x - player.getX();
            int dy = mouseLocation.y - player.getY();
            bullets.add(player.shoot(Math.atan2(dy, dx)));
            mouseControls.shoot = false;
        }

        //updates both enemy and player bullets position/status
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).move(ticks);
            if (bullets.get(i).x <= 0 || bullets.get(i).y <= 0 || bullets.get(i).x >= Main.SCREEN_WIDTH || bullets.get(i).y >= Main.SCREEN_HEIGHT) {
                bullets.remove(bullets.get(i));
            }
        }
        for (int i = 0; i < enemyBullets.size(); i++) {
            enemyBullets.get(i).move(ticks);
            if (enemyBullets.get(i).x <= 0 || enemyBullets.get(i).y <= 0 || enemyBullets.get(i).x >= Main.SCREEN_WIDTH || enemyBullets.get(i).y >= Main.SCREEN_HEIGHT) {
                enemyBullets.remove(enemyBullets.get(i));
            }
        }

        //checks collision
        if (player.checkForCollision(enemyBullets)) {
            player.die();
        }

        //prints out the status of the game once in a while
        if (ticks % 900 == 0) {
            System.out.println("Player size: " + players.size());
            System.out.println("Server queue size: " + serverQueue.size());
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
        bullets.forEach((bullet) -> {
            bullet.render(g2d, showHitBoxes);
        });
        enemyBullets.forEach((bullet) -> {
            bullet.render(g2d, showHitBoxes);
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
            repaint();
            frames++;
            ticks += 1;
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
