package com.TheoAslev.level;

import java.util.HashMap;
import java.util.Map;

public enum TileTexture{
    SOLID_STONE_BLOCK(0),
    SOLID_GRASS_BLOCK(1),
    PLATFORM_STONE_BLOCK(2),
    GHOST_STONE_BLOCK(3),
    GHOST_TRANSPARENT(4);

    private int value;
    private static Map<Integer,TileTexture> textureMap = new HashMap<>();

    private TileTexture(int value){
        this.value = value;
    }

    static {
        for (TileTexture texture : TileTexture.values()) {
            textureMap.put(texture.ordinal(), texture);
        }
    }

    public static TileTexture getInstance(int instance) {
        return textureMap.get(instance);
    }

}
