package com.TheoAslev.eventListeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyControls implements KeyListener {

    public boolean right, left, jump, isHitBoxesVisible;

    @Override
    public void keyTyped(KeyEvent e) {
        //does nothing
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //keyboard input when pressing buttons
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_A) left = true;
        if (keyCode == KeyEvent.VK_D) right = true;
        if (keyCode == KeyEvent.VK_SPACE) jump = true;
        if (keyCode == KeyEvent.VK_H && !isHitBoxesVisible) {
            isHitBoxesVisible = true;
        } else if (keyCode == KeyEvent.VK_H) {
            isHitBoxesVisible = false;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //keyboards input when releasing button
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_A) left = false;
        if (keyCode == KeyEvent.VK_D) right = false;
        if (keyCode == KeyEvent.VK_SPACE) jump = false;
    }
}
