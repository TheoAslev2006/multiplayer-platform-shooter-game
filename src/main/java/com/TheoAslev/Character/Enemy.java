package com.TheoAslev.Character;

import com.TheoAslev.level.Tile;

import java.awt.*;
import java.util.HashMap;

public class Enemy extends Character implements Controls{
    public boolean movingRight = false;
    boolean movingLeft = false;
    int tick;
    String name = "You";

    public Enemy(int tick){
        x = 100;
        this.tick = tick;
        loadCharacter();
    }

    @Override
    public void moveRight(double speed) {
        velX = 1;
    }

    @Override
    public void moveLeft(double speed) {
        velX = -1;

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
        y -= 2;
    }

    public void renderPlayer(Graphics2D g2d){
        renderCharacter(g2d, true, false , name);
    }

}
