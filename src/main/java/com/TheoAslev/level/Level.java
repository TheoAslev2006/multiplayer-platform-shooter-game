package com.TheoAslev.level;

import com.TheoAslev.utils.FileReader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

//responsible for loading a level based on an image model with specific colours that dictate where the specific block is going to be drawn;
public class Level {
    Levels levels;
    String[] filePaths = {"src\\main\\resources\\model\\level\\map.png", "", ""};
    BufferedImage bufferedImage;
    BufferedImage texture;
    public HashMap<String, Tile> tileMap = new HashMap<>();
    byte[] bytes;

    public Level(Levels levels) throws IOException {
        bytes = new byte[30 * 40];
        this.levels = levels;
        //load a buffered image from specific filepath depending on level chosen
        switch (levels) {
            case Level1:
                bufferedImage = FileReader.loadFile(filePaths[0]);
                break;
            case Level2:
                bufferedImage = FileReader.loadFile(filePaths[1]);
                break;
            case Level3:
                bufferedImage = FileReader.loadFile(filePaths[2]);
                break;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //each pixel is read and converted into a texture from the buffered image which behaves as a model for the map
        for (int y = 0; y < 30; y++) {
            for (int x = 0; x < 40; x++) {
                //picks out a specific colour based on bit location of the colour
                int pixelColour = bufferedImage.getRGB(x, y);
                int red = (pixelColour >> 16) & 0xFF;
                int green = (pixelColour >> 8) & 0xFF;
                int blue = pixelColour & 0xFF;
                if (blue == 0 && green == 0 && red == 0) {
                    byteArrayOutputStream.write(0);
                } else if (blue == 255 && green == 255 && red == 255) {
                    byteArrayOutputStream.write(1);
                } else if (red == 255 && green == 0 && blue == 0) {
                    byteArrayOutputStream.write(2);
                } else if (red == 0 && green == 255 && blue == 0) {
                    byteArrayOutputStream.write(3);
                } else if (red == 0 && green == 0 && blue == 255) {
                    byteArrayOutputStream.write(4);
                }
            }
        }
        bytes = byteArrayOutputStream.toByteArray();
    }

    public void render(Graphics2D g2d) {
        //renders the tiles to the screen
        for (int y = 0; y < 30; y++) {
            for (int x = 0; x < 40; x++) {
                String tileKey = x + "," + y;
                TileTexture tileTexture = TileTexture.getInstance(bytes[y * 40 + x]);
                if (!tileMap.containsKey(tileKey)) {
                    tileMap.put(tileKey, new Tile(tileTexture));
                }
                BufferedImage tileImage = tileMap.get(tileKey).bufferedImage;
                if (tileImage != null) {
                    g2d.drawImage(tileImage, x * 32, y * 32, 32, 32, null);
                }
            }
        }
    }
}
