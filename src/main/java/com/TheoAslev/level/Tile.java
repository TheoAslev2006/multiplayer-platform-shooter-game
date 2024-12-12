package com.TheoAslev.level;

import com.TheoAslev.server.Server;
import com.TheoAslev.utils.FileReader;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

public class Tile{

    boolean platform;
    boolean ghost;
    public BufferedImage bufferedImage;
    public Tile(TileTexture tileTexture){
        bufferedImage = null;
        String filePath = "src\\main\\resources\\textures\\tiles\\tileSet.png";
        //initializes buffered image with said tile texture
        switch (tileTexture){
            //initializes solid stone block
            case SOLID_STONE_BLOCK -> {
                platform = false;
                ghost = false;
                try {
                    bufferedImage = FileReader.loadTileSet(filePath, 32, 32, 5)[TileTexture.SOLID_STONE_BLOCK.ordinal()];
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            //initializes solid grass block
            case SOLID_GRASS_BLOCK -> {
                platform = false;
                ghost = false;
                try {
                    bufferedImage = FileReader.loadTileSet(filePath, 32, 32, 5)[TileTexture.SOLID_GRASS_BLOCK.ordinal()];
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            //initializes platform block with stone texture
            case PLATFORM_STONE_BLOCK -> {
                platform = true;
                ghost = false;
                try {
                    bufferedImage = FileReader.loadTileSet(filePath, 32, 32, 5)[TileTexture.PLATFORM_STONE_BLOCK.ordinal()];
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            //initializes ghost block with stone texture
            case GHOST_STONE_BLOCK -> {
                platform = false;
                ghost = true;
                try {
                    bufferedImage = FileReader.loadTileSet(filePath, 32, 32, 5)[TileTexture.PLATFORM_STONE_BLOCK.ordinal()];
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            //initializes ghost block with no texture
            case GHOST_TRANSPARENT -> {
                platform = false;
                ghost = true;
            }
        }

    }
    public boolean isPlatform() {
        return platform;
    }

    public boolean isGhost() {
        return ghost;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }
}
