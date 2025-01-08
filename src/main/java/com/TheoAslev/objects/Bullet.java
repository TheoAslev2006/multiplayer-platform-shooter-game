package com.TheoAslev.objects;

import com.TheoAslev.graphics.Game;
import com.TheoAslev.utils.FileReader;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

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
        affineTransform = AffineTransform.getRotateInstance(radians, 13, 13);
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
        g2d.drawImage(affineTransformOp.filter(bufferedImage, null), x + 3, y, null);
        if (localBullet)
            g2d.drawRect(rectX + 11, rectY + 10, 10, 10);
        if (showHitBox) {
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, 32, 32);
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