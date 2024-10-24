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

    int health;
    boolean dead;
    boolean jump;
    BufferedImage characterImage;
    BufferedImage[] animation = new BufferedImage[5];
    String[] filePath = { "src\\main\\resources\\textures\\tiles\\CharacterNull.png" ,"animation"};
    public void loadCharacter(){
        boolean loaded = false;
        int instance = 0;
        while (!loaded)
            try {
                characterImage = FileReader.loadFile(filePath[0]);
                //animation = FileReader.loadTileSet(filePath[1], 32, 32, 5);
                loaded = true;
            } catch (IOException e) {
                instance++;
                if (instance == 5) throw new RuntimeException();
            }
    }
    public void renderCharacter(Graphics2D g2d, boolean movingRight, boolean movingLeft, String name){
//        if (movingLeft){
//            if (tick % 2 == 1)
//                g2d.drawImage(animation[0], x, y, null);
//            if (tick % 2 == 0)
//                g2d.drawImage(animation[1], x, y, null);
//            tick++;
//        }
//        if (movingRight){
//            if (tick % 2 == 1)
//                g2d.drawImage(animation[2], x, y, null);
//            if (tick % 2 == 0)
//                g2d.drawImage(animation[3], x, y, null);
//            tick++;
//        }
//        if (jump){
//            g2d.drawImage(animation[4], x, y, null);
//        }
//        else {
//            g2d.drawImage(characterImage, x, y, null);
//        }
        g2d.drawString(name, x, y+50);
        g2d.drawImage(characterImage, x, y , null);
    }
}
