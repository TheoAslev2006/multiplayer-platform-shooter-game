package com.TheoAslev.Character;

import com.TheoAslev.level.Tile;
import com.TheoAslev.objects.Bullet;

import java.awt.*;
import java.util.HashMap;

public interface Controls {
    public void moveRight(double speed);
    public void moveLeft(double speed);
    public void jump();
    public Bullet shoot(double radians);
    public void die();
    public void fall();
}