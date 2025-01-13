package com.TheoAslev.entity;

public interface Controls {
    public void moveRight(double speed);

    public void moveLeft(double speed);

    public void jump();

    public Bullet shoot(double radians);

    public void die();

    public void calculatePlayerPosition();

    public void moveDown();
}