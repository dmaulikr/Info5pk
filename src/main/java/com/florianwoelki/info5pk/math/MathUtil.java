package com.florianwoelki.info5pk.math;

import java.util.Random;

/**
 * Created by Florian Woelki on 16.11.16.
 */
public class MathUtil {

    public static final float PI = 3.1415926535f;
    public static final Random random = new Random();

    public static float abs(float value) {
        return Math.abs(value);
    }

    public static float sin(float value) {
        return (float) Math.sin(value);
    }

    public static float cos(float value) {
        return (float) Math.cos(value);
    }

    public static float sigmoid(float x) {
        return (float) (1 / (1 + Math.exp(-x)));
    }

    public static float clamp(float value) {
        if(value < 0) return 0;
        if(value > 1) return 1;
        return value;
    }

    public static float max(float a, float b) {
        return a > b ? a : b;
    }

    public static float clampNegativePosition(float value) {
        if(value < -1) return -1;
        if(value > 1) return 1;
        return value;
    }

}
