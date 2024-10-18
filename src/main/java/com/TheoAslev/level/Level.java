package com.TheoAslev.level;

import com.TheoAslev.utils.FileReader;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Level {
    Levels levels;
    String[] filePaths = {"src\\main\\resources\\textures\\tiles\\test.png","",""};
    BufferedImage bufferedImage;

    public Level(Levels levels) throws IOException {
        byte[] bytes;
        //load a buffered image from specific filepath depending on level chosen
        switch (levels){
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
        //each pixel is read and converted into a colour from the buffered image
        //TODO change the x bounds and y bounds for the for loops in order to take bigger images
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                int pixel = bufferedImage.getRGB(x , y);
                int red = (pixel >> 16) & 0xFF;
                int green =  (pixel >> 8) & 0xFF;
                int blue =  pixel & 0xFF;
                if ( blue == 255 && green == 255 && red == 255){
                    byteArrayOutputStream.write(1);
                } else if ( blue == 0 && green == 0 && red == 0){
                    byteArrayOutputStream.write(2);
                } else if (red == 255 && green == 0 && blue == 0){
                    byteArrayOutputStream.write(3);
                }
            }
        }
        //the colours written into the output stream are converted into a byte array
        bytes = byteArrayOutputStream.toByteArray();
        //prints the colours as a test fir tg
        System.out.println(Arrays.toString(bytes));
        //TODO add an render for the map with a tile class for different tiles
        for (int i = 0; i < bytes.length; i++) {

        }
    }
}
