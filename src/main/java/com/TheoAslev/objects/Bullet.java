package com.TheoAslev.objects;

import com.TheoAslev.Main;
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
    final double velY;
    final double velX;
    final double radians;
    String filepath = "src\\main\\resources\\textures\\objects\\bullet.png";
    BufferedImage bufferedImage;
    AffineTransform affineTransform;
    AffineTransformOp affineTransformOp;
    public Bullet(double velX, double velY, double degrees, Point orgin) {
        this.velY = velY;
        this.velX = velX;
        // calculates x and y cords based on where the mouse is
        x = (int) (orgin.x + Math.cos(Math.toRadians(degrees)) * 30);
        y = (int) (orgin.y + Math.sin(Math.toRadians(degrees)) * 30);
        // stores initial x and y coordinates for antoher object
        rectX = x;
        rectY = y;
        // converts degrees to radians
        this.radians = Math.toRadians(degrees);
        // calculates rotation of bullet sprite
        affineTransform = AffineTransform.getRotateInstance(radians, 13, 13);
        affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_BILINEAR);
        // loads bullet sprite
        try {
            bufferedImage = FileReader.loadFile(filepath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Bam!");

    }

    public void render(Graphics2D g2d){
        //renders bullet and square for viewing bullet angle
        g2d.drawImage(affineTransformOp.filter(bufferedImage, null), x + 3 , y , null);
        g2d.drawRect(rectX + 11, rectY + 10, 10, 10);
    }
    public void move(long runtime){
        //moves bullet every 2 ticks for better looks
        if (runtime % 2 == 0) {
            x += (int) velX;
            y += (int) velY;
        }
    }
}
