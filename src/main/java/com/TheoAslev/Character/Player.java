package com.TheoAslev.Character;

import com.TheoAslev.Main;
import com.TheoAslev.level.Tile;
import com.TheoAslev.objects.Bullet;

import java.awt.*;
import java.util.HashMap;

public class Player extends Character implements Controls{
    boolean movingRight = false;
    boolean movingLeft = false;
    boolean onGround = false;
    String name = "You";
    final int startJumpVelocity = -5;
    HashMap<String, Tile> tileMap;
    public Player(HashMap<String, Tile> tileMap){
        this.tileMap = tileMap;
        x = 200;
        y = 200;
        loadCharacter();
    }

    @Override
    public void moveRight(double speed) {
        velX += speed/5;
        velX = Math.max(velX, 5);
    }

    @Override
    public void moveLeft(double speed) {
        velX -= speed/5;
        velX = Math.min(velX, -5);
    }

    @Override
    public void jump() {
        if (onGround){
            velY = -10;
            onGround = false;
        }
    }
    @Override
    public Bullet shoot(double radians) {
        radians = Math.toRadians(radians);
        if (radians <= 0)
            radians += Math.toRadians(360);
        System.out.println(radians);
        System.out.println("speed: " + Math.cos(radians) * 10 + " " + Math.sin(radians) * 10);
        return new Bullet( Math.cos(radians) * 100, Math.sin(radians) * 100, Math.toDegrees(radians), new Point(x, y));

    }

    @Override
    public void die() {

    }

    @Override
    public void fall() {
        String tileKey = x / 32 + "," + (y / 32 + 1);
        if (tileMap.get(tileKey) != null && velY != 0){
            if (!tileMap.get(tileKey).isGhost()) {
                if (!tileMap.get(tileKey).isPlatform()){
                    if (((y + velY + 0.1) % 32 != 0) && velY >= 0) {
                        y = y + (int) (velY - ((y + velY + 0.1) % 32));
                        velY = 0;
                        onGround = true;
                    }
                }
                else if (tileMap.get(tileKey).isPlatform()){
                    if (((y + velY + 0.1) % 32 != 0) && velY >= 0) {
                        y = y + (int) (velY - ((y + velY + 0.1) % 32));
                        velY = 0;
                        onGround = true;
                    }
                }
            }
        }
        if (tileMap.get(tileKey) != null){
            if (tileMap.get(tileKey).isGhost()){
                onGround = false;
            }
        } else {
            onGround = false;
        }

    }

    public void renderPlayer(Graphics2D g2d){
        renderCharacter(g2d, true, false, name);
    }
    public void updatePlayer(){
        velX *= 0.9;
        x += (int) velX;
        if (!onGround){
            velY += 0.2;
            y += (int) velY;
        }
    }

}
