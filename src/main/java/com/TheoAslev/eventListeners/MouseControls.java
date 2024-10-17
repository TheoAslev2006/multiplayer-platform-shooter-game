package com.TheoAslev.eventListeners;

import com.TheoAslev.Main;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseControls implements MouseListener {
    boolean shoot;
    int monitorWidth = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getWidth();
    int monitorHeight = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight();
    boolean inWindow = (
            (MouseInfo.getPointerInfo().getLocation().x <= (monitorWidth/ 2 + Main.SCREEN_WIDTH / 2)
            && MouseInfo.getPointerInfo().getLocation().x >= (monitorWidth / 2 - Main.SCREEN_WIDTH / 2))
            &&(MouseInfo.getPointerInfo().getLocation().y <= (monitorHeight/ 2 + Main.SCREEN_HEIGHT / 2)
            && MouseInfo.getPointerInfo().getLocation().y >= (monitorHeight / 2 - Main.SCREEN_HEIGHT / 2)) );
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (inWindow)
            shoot = true;

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        inWindow = true;
        System.out.println("Entered Screen");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        inWindow = false;
        System.out.println("Exited Screen");
    }
}
