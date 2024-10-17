package com.TheoAslev.main.eventListeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyControls implements KeyListener {

    boolean right,left,jump;

    @Override
    public void keyTyped(KeyEvent e) {
        //does nothing
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_A) left = true;
        if (keyCode == KeyEvent.VK_D) right = true;
        if (keyCode == KeyEvent.VK_SPACE) jump = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_A) left = false;
        if (keyCode == KeyEvent.VK_D) right = false;
        if (keyCode == KeyEvent.VK_SPACE) jump = false;
    }
}
