package com.TheoAslev.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileReader {
    //This function loads an image and returns it
    public static BufferedImage loadFile(String fileName) throws IOException {
        File file = new File(fileName);
        return ImageIO.read(file);
    }

    //This function loads an image and turns it into an image array with a length based of the tile dimensions
    public static BufferedImage[] loadTileSet(String fileName, int tileWidth, int tileHeight, int count) throws IOException{
        File file = new File(fileName);
        BufferedImage tileSet = ImageIO.read(file);
        BufferedImage[] tileTextures = new BufferedImage[count];
        for (int i = 1; i < tileSet.getHeight() / tileHeight; i++) {
            for (int j = 0; j < tileSet.getWidth() / tileWidth; j++) {
                tileTextures[j * i] = tileSet.getSubimage(j * tileWidth, i * tileHeight, tileWidth, tileHeight);
            }
        }
        return tileTextures;
    }
}