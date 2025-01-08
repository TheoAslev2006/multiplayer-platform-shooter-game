package com.TheoAslev.utils;

public class HitBox {
    //utility class
    public static boolean isColliding(int[] hitBoxVertices, int[] hitBoxVertices_1) {
        //checks for collision with overlapping
        if (hitBoxVertices[2] > hitBoxVertices_1[0] && hitBoxVertices[0] < hitBoxVertices_1[2] &&
                hitBoxVertices[5] > hitBoxVertices_1[1] && hitBoxVertices[1] < hitBoxVertices_1[5]) {
            return true;
        }
        return false;
    }

    public static int[] createHitBoxVertices(int x, int y, int width, int height) {
        return new int[]{
                x, y, x + width, y, x, y + height, x + width, y + height
        };
    }
}
