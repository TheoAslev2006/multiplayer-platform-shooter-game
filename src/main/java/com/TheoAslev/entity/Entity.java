package com.TheoAslev.entity;

import com.TheoAslev.utils.FileReader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;


//this class is used for entities such as the player
public class Entity {
    int x;
    int y;
    double velY;
    double velX;
    int entityAnimationInstance = 0;
    BufferedImage entityImage;
    BufferedImage[] animation = new BufferedImage[3];
    String[] filePath = {
            "src\\main\\resources\\textures\\character\\CharacterNull.png"
            , "src\\main\\resources\\textures\\character\\Character_not_moving.png"
            , "src\\main\\resources\\textures\\character\\Character_moving_1.png"
            , "src\\main\\resources\\textures\\character\\Character_moving_2.png"
    };

    public void loadEntity() {
        //loads images from file with different animation stages
        boolean loaded = false;
        int instance = 0;
        while (!loaded) {
            try {
                animation[0] = FileReader.loadFile(filePath[1]);
                animation[1] = FileReader.loadFile(filePath[2]);
                animation[2] = FileReader.loadFile(filePath[3]);
                loaded = true;
            } catch (IOException e) {
                instance++;
                if (instance == 5) {
                    try {
                        animation[0] = FileReader.loadFile(filePath[entityAnimationInstance + 1]);
                        loaded = true;
                    } catch (IOException ex) {
                        try {
                            entityImage = FileReader.loadFile(filePath[0]);
                        } catch (IOException exc) {
                            throw new RuntimeException(exc);
                        }
                    }
                }
            }
        }
    }

    public void tick(long threadRunTime) {
        //changes the player animation when moving and based on a long acting as cooldown
        if (velX != 0 && threadRunTime % 20 == 0) {
            entityAnimationInstance = 2;
        } else if (velX != 0 && threadRunTime % 10 == 0) {
            entityAnimationInstance = 1;
        }
        if (velX <= 0.7 && velX >= -0.7) {
            entityAnimationInstance = 0;
        }

    }

    public void renderEntity(Graphics2D g2d, Point nameOffset, String name) {
        //draws character onto the screen with offsets
        g2d.drawString(name, x + nameOffset.x, y - nameOffset.y);
        g2d.drawImage(animation[entityAnimationInstance], x - 16, y, null);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

}

