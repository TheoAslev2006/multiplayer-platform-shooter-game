package com.TheoAslev.Character;

import com.TheoAslev.level.Tile;

import java.awt.*;
import java.util.HashMap;

public class Player extends Character implements Controls{
    boolean movingRight = false;
    boolean movingLeft = false;
    boolean onGround = false;
    String name = "You";
    final int startJumpVelocity = -5;
    HashMap<String, Tile> tileMap;
    public Player( HashMap<String, Tile> tileMap){
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

    }
    @Override
    public void shoot() {

    }

    @Override
    public void die() {

    }

    @Override
    public void fall() {
        String tileKey = x / 32 + "," + (y / 32 + 1);
        if (tileMap.get(tileKey) != null && velY != 0){
            if (!tileMap.get(tileKey).isGhost()){
                if ( ((y + velY + 0.1) % 32!= 0)){
                    y = y +(int) (velY - ((y + velY + 0.1) % 32));
                    velY = 0;
                    onGround = true;
                }
            }
        }
    }

    public void renderPlayer(Graphics2D g2d){
        renderCharacter(g2d, true, false, name);
    }
    public void updatePlayer(){
        velX *= 0.9;
        x += (int) velX;
        if (!onGround){
            velY += 0.1;
            y += (int) velY;
        }
    }

}
