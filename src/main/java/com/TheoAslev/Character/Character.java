package com.TheoAslev.Character;

import com.TheoAslev.utils.FileReader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Character{
    int x;
    int y;
    double velY;
    double velX;
    int characterAnimationInstance = 0;
    int health;
    boolean dead;
    boolean jump;
    BufferedImage characterImage;
    BufferedImage[] animation = new BufferedImage[3];
    String[] filePath = {
            "src\\main\\resources\\textures\\character\\CharacterNull.png"
            ,"src\\main\\resources\\textures\\character\\Character_not_moving.png"
            ,"src\\main\\resources\\textures\\character\\Character_moving_1.png"
            ,"src\\main\\resources\\textures\\character\\Character_moving_2.png"
    };
    public void loadCharacter(){
        boolean loaded = false;
        int instance = 0;
        while (!loaded){
            try {
                animation[0] = FileReader.loadFile(filePath[1]);
                animation[1] = FileReader.loadFile(filePath[2]);
                animation[2] = FileReader.loadFile(filePath[3]);
                loaded = true;
            } catch (IOException e) {
                instance++;
                if (instance == 5){
                    try {
                        animation[0] = FileReader.loadFile(filePath[characterAnimationInstance + 1]);
                        loaded = true;
                    } catch (IOException ex) {
                        try {
                            characterImage = FileReader.loadFile(filePath[0]);
                        } catch (IOException exc) {
                            throw new RuntimeException(exc);
                        }
                    }
                }
            }
        }
    }
    public void tick(long threadRunTime){
        if (velX != 0 && threadRunTime % 20 == 0){
            characterAnimationInstance = 2;
        }else if (velX != 0 && threadRunTime % 10 == 0){
            characterAnimationInstance = 1;
        }
        if (velX <= 0.7 && velX >= -0.7){
            characterAnimationInstance = 0;
        }

    }
    public void renderCharacter(Graphics2D g2d, boolean movingRight, boolean movingLeft, String name){
        g2d.drawString(name, x + 7, y-10);
        g2d.drawImage(animation[characterAnimationInstance], x, y , null);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

