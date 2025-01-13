package com.TheoAslev.entity;

import com.TheoAslev.utils.FileReader;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

//bullet class that creates a bullet with said angle and moves till it hits the end of the map
public class Bullet {
    public int x;
    public int y;
    int rectX;
    int rectY;
    public final double velY;
    public final double velX;
    public final double radians;
    final boolean localBullet;
    String filepath = "src\\main\\resources\\textures\\objects\\bullet.png";
    BufferedImage bufferedImage;
    AffineTransform affineTransform;
    AffineTransformOp affineTransformOp;

    public Bullet(double velX, double velY, double radians, Point orgin) {
        //initiates the local bullet and calculations for rotation
        this.radians = radians;
        this.velY = velY;
        this.velX = velX;
        x = (int) (orgin.x + Math.cos(radians) * 30);
        y = (int) (orgin.y + Math.sin(radians) * 30);
        rectX = x;
        rectY = y;
        //rotates the bullet texture with a bi-linear transformation
        affineTransform = AffineTransform.getRotateInstance(radians, 16, 16);
        affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_BILINEAR);
        try {
            bufferedImage = FileReader.loadFile(filepath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        localBullet = true;
    }

    public void render(Graphics2D g2d, boolean showHitBox) {
        //renders bullet and square for viewing bullet angle
        g2d.drawImage(affineTransformOp.filter(bufferedImage, null), x, y, null);
        if (localBullet)
            g2d.drawRect(rectX - 8, rectY + 16, 10, 10);
        if (showHitBox) {
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x + 8, y + 8, 16, 16);
        }
    }

    public void move(long runtime) {
        //moves bullet every 2 ticks for better looks
        if (runtime % 2 == 0) {
            x += (int) velX;
            y += (int) velY;
        }
    }
}