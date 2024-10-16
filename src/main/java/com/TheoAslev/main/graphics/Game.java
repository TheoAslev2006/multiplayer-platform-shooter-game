package com.TheoAslev.main.graphics;

import javax.swing.*;
import java.awt.*;

public class Game extends JPanel{
    final int screenWidth;
    final int screenHeight;
    public Game(int screenWidth, int screenHeight){
        //initialization of JPanel
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setDoubleBuffered(true);
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }
}
