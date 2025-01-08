package com.TheoAslev.character;

import com.TheoAslev.objects.Bullet;

public interface Controls {
    public void moveRight(double speed);

    public void moveLeft(double speed);

    public void jump();

    public Bullet shoot(double radians);

    public void die();

    public void calculatePlayerPosition();
}