package com.TheoAslev.character;

import com.TheoAslev.graphics.Game;
import com.TheoAslev.level.Tile;
import com.TheoAslev.objects.Bullet;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Player extends Character implements Controls, Serializable {
    boolean movingRight = false;
    boolean movingLeft = false;
    boolean onGround = false;
    String name;
    final int startJumpVelocity = -5;
    HashMap<String, Tile> tileMap;
    Game game;

    public Player(Game game, String name) {
        this.game = game;
        this.name = name;
        this.tileMap = game.level.tileMap;
        x = 200;
        y = 200;
        loadCharacter();
    }

    @Override
    public void moveRight(double speed) {
        velX += speed / 5;
        velX = Math.max(velX, 5);
    }

    @Override
    public void moveLeft(double speed) {
        velX -= speed / 5;
        velX = Math.min(velX, -5);
    }

    @Override
    public void jump() {
        if (onGround) {
            velY = -10;
            onGround = false;
        }
    }

    @Override
    public Bullet shoot(double radians) {
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

    }

    @Override
    public void fall() {
        String tileKey = x / 32 + "," + (y / 32 + 1);
        if (tileMap.get(tileKey) != null && velY != 0) {
            if (!tileMap.get(tileKey).isGhost()) {
                if (!tileMap.get(tileKey).isPlatform()) {
                    if (((y + velY + 0.1) % 32 != 0) && velY >= 0) {
                        y = y + (int) (velY - ((y + velY + 0.1) % 32));
                        velY = 0;
                        onGround = true;
                    }
                } else if (tileMap.get(tileKey).isPlatform()) {
                    if (((y + velY + 0.1) % 32 != 0) && velY >= 0) {
                        y = y + (int) (velY - ((y + velY + 0.1) % 32));
                        velY = 0;
                        onGround = true;
                    }
                }
            }
        }
        if (tileMap.get(tileKey) != null) {
            if (tileMap.get(tileKey).isGhost()) {
                onGround = false;
            }
        } else {
            onGround = false;
        }

    }

    public void renderPlayer(Graphics2D g2d) {
        renderCharacter(g2d, true, false, name);
    }

    public void updatePlayer() {
        velX *= 0.9;
        x += (int) velX;
        if (!onGround) {
            velY += 0.2;
            y += (int) velY;
        }
    }

    public HashMap<String, Tile> getTileMap() {
        return tileMap;
    }

    public String getName() {
        return name;
    }
}
