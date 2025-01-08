package com.TheoAslev.character;

import com.TheoAslev.graphics.Game;
import com.TheoAslev.level.Tile;
import com.TheoAslev.objects.Bullet;
import com.TheoAslev.utils.HitBox;

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
        //initiates the player klass
        this.game = game;
        this.name = name;
        this.tileMap = game.level.tileMap;
        x = 200;
        y = 200;
        loadCharacter();
    }

    @Override
    public void moveRight(double speed) {
        //creates a positive horizontal velocity in order to move to the right
        velX += speed / 5;
        velX = Math.max(velX, 5);
    }

    @Override
    public void moveLeft(double speed) {
        //creates a negative horizontal velocity in order to move to the left
        velX -= speed / 5;
        velX = Math.min(velX, -5);
    }

    @Override
    public void jump() {
        //creates a negative vertical velocity in order to simulate jumping
        if (onGround) {
            velY = -10;
            onGround = false;
        }
    }

    @Override
    public Bullet shoot(double radians) {
        //calculates the velocity, rotation and origin of the bullet
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
    }

    @Override
    public void calculatePlayerPosition() {
        //algorithm to simulate falling logic with the given tile-map
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
        //removes the ability for the player to go outside the map
        if (x <= 0)
            x += 10;
        if (x + 32 >= game.screenWidth)
            x -= 10;
    }

    public void renderPlayer(Graphics2D g2d) {
        //renders character and hit box
        renderCharacter(g2d, true, false, name);
        if (game.showHitBoxes) {
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, 32, 32);
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
            if (HitBox.isColliding(HitBox.createHitBoxVertices(x, y, 32, 32), HitBox.createHitBoxVertices(bullets.get(i).x, bullets.get(i).y, 32, 32))) {
                return true;
            }
            if (HitBox.isColliding(HitBox.createHitBoxVertices(x + bullets.get(i).x, y + bullets.get(i).y, 32, 32), HitBox.createHitBoxVertices(bullets.get(i).x, bullets.get(i).y, 32, 32))) {
                return true;
            }
            if (HitBox.isColliding(HitBox.createHitBoxVertices(x - bullets.get(i).x, y - bullets.get(i).y, 32, 32), HitBox.createHitBoxVertices(bullets.get(i).x, bullets.get(i).y, 32, 32))) {
                return true;
            }
        }
        return false;
    }

    public HashMap<String, Tile> getTileMap() {
        return tileMap;
    }

    public String getName() {
        return name;
    }
}
