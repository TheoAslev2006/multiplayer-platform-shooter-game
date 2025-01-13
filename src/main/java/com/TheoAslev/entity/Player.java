package com.TheoAslev.entity;

import com.TheoAslev.graphics.Game;
import com.TheoAslev.level.Tile;
import com.TheoAslev.utils.HitBox;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

//player class that extends entity class and is a playable entity with physics;
public class Player extends Entity implements Controls, Serializable {
    boolean onGround = false;
    boolean jumping = false;
    boolean descend = false;
    boolean falledThroughFirstPlatform = false;
    final String name;
    final int startJumpVelocity = -9;
    final int maxHorizontalSpeed = 5;
    public int health = 100;
    final HashMap<String, Tile> tileMap;
    Game game;


    public Player(Game game, String name) {
        //initiates the player class
        this.game = game;
        this.name = name;
        this.tileMap = game.level.tileMap;
        x = 200;
        y = 200;
        loadEntity();
    }

    @Override
    public void moveRight(double speed) {
        //creates a positive horizontal velocity in order to move to the right
        velX += speed / 20;
        velX = Math.max(velX, maxHorizontalSpeed);
    }

    @Override
    public void moveLeft(double speed) {
        //creates a negative horizontal velocity in order to move to the left
        velX -= speed / 20;
        velX = Math.min(velX, maxHorizontalSpeed * -1);
    }

    @Override
    public void moveDown() {
        if (onGround) {
            onGround = false;
            descend = true;
            falledThroughFirstPlatform = false;
        }
    }

    @Override
    public void jump() {
        //creates a negative vertical velocity in order to simulate jumping
        if (onGround) {
            velY = startJumpVelocity;
            onGround = false;
            jumping = true;
        }
    }

    @Override
    public Bullet shoot(double radians) {
        //calculates the velocity, rotation and origin of the bullet and then adds it to the server
        if (radians <= 0)
            radians += Math.toRadians(360);
        else if (radians <= Math.PI * 2)
            radians -= Math.toRadians(360);
        System.out.println(radians);
        System.out.println("speed: " + Math.cos(radians) * 10 + " " + Math.sin(radians) * 10);
        game.bulletsToServer.add(x + "," + y + "," + radians);
        return new Bullet(Math.cos(radians) * 100, Math.sin(radians) * 100, radians, new Point(x, y));

    }

    @Override
    public void die() {
        //reverts the player back to its spawn position in order to simulate losing
        x = 200;
        y = 200;
        velX = 0;
        velY = 0;
        health = 100;
    }

    @Override
    public void calculatePlayerPosition() {
        //algorithm to simulate falling logic with the given tile-map for reference
        String tileKey1 = x / 32 + "," + (y / 32 + 1);
        if (tileMap.get(tileKey1) != null && velY != 0) {
            if (!tileMap.get(tileKey1).isGhost()) {
                if (!tileMap.get(tileKey1).isPlatform()) {
                    if (((y + velY + 0.1) % 32 != 0) && velY >= 0) {
                        y = y + (int) (velY - ((y + velY) % 32));
                        velY = 0;
                        onGround = true;
                        descend = false;
                    }
                } else if (tileMap.get(tileKey1).isPlatform()) {
                    if (((y + velY + 0.1) % 32 != 0) && velY >= 0) {
                        if (!descend) {
                            y = y + (int) (velY - ((y + velY) % 32));
                            velY = 0;
                            onGround = true;
                        }
                    }
                }
            }
        }

        if (tileMap.get(tileKey1) != null) {
            if (tileMap.get(tileKey1).isGhost()) {
                onGround = false;
            }
        } else {
            jumping = false;
            onGround = false;
        }
        //removes the ability for the player to go outside the map
        if (x <= 0)
            x += 10;
        if (x >= game.screenWidth)
            x -= 10;
    }

    public void renderPlayer(Graphics2D g2d, boolean isHosting) {
        //renders character and hit box
        renderEntity(g2d, new Point(-14, 16), name);
        if (game.showHitBoxes) {
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x - 16, y, 32, 32);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x - (x % 32), y - (y % 32) + 32, 32, 32);
        }
        if (isHosting) {
            g2d.drawRect(x - 26, y - 10, 52, 6);
            g2d.setColor(Color.GREEN);
            g2d.fillRect(x - 25, y - 9, health / 2 + 1, 5);
            g2d.setColor(Color.BLACK);
        }
    }

    public void updatePlayer() {
        //updates players position based on velocity with some variables such as gravity
        velX *= 0.9;
        x += (int) velX;
        if (!onGround) {
            velY += 0.2;
            y += (int) velY;
        }
    }

    public boolean checkForCollision(ArrayList<Bullet> bullets) {
        //checks for player colliding with enemy bullets
        for (int i = 0; i < bullets.size(); i++) {
            if (HitBox.isColliding(HitBox.createHitBoxVertices(x, y, 32, 32), HitBox.createHitBoxVertices(bullets.get(i).x, bullets.get(i).y, 16, 16))) {
                bullets.remove(i);
                return true;
            }
            for (int j = 0; j <= 10; j++) {
                if (HitBox.isColliding(HitBox.createHitBoxVertices(x, y, 32, 32), HitBox.createHitBoxVertices((int) (bullets.get(i).x + (bullets.get(i).velX / 10) * j), (int) (bullets.get(i).y + (bullets.get(i).velY / 10) * j), 16, 16))) {
                    bullets.remove(i);
                    return true;
                }
            }

        }
        return false;
    }
}
