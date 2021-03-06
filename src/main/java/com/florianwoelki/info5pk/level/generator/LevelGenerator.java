package com.florianwoelki.info5pk.level.generator;

import com.florianwoelki.info5pk.math.MathUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Florian Woelki on 19.11.16.
 */
public class LevelGenerator {

    public int octaves = 8;
    public int startFrequencyX = 2;
    public int startFrequencyY = 2;
    public final int WIDTH;
    public final int HEIGHT;

    public float[][] map;

    public LevelGenerator(int width, int height) {
        this.WIDTH = width;
        this.HEIGHT = height;

        map = new float[width][height];
    }

    public void calculate() {
        int currentFrequencyX = startFrequencyX;
        int currentFrequencyY = startFrequencyY;

        float currentAlpha = 1;

        for(int oc = 0; oc < octaves; oc++) {
            if(oc > 0) {
                currentFrequencyX *= 2;
                currentFrequencyY *= 2;
                currentAlpha /= 2;
            }

            float[][] discretePoints = new float[currentFrequencyX + 1][currentFrequencyY + 1];
            for(int i = 0; i < currentFrequencyX; i++) {
                for(int k = 0; k < currentFrequencyY; k++) {
                    discretePoints[i][k] = (float) (MathUtil.random.nextDouble() * currentAlpha);
                }
            }

            for(int i = 0; i < WIDTH; i++) {
                for(int k = 0; k < HEIGHT; k++) {
                    float currentX = i / (float) WIDTH * currentFrequencyX;
                    float currentY = k / (float) HEIGHT * currentFrequencyY;

                    int indexX = (int) currentX;
                    int indexY = (int) currentY;

                    float w0 = interpolate(discretePoints[indexX][indexY], discretePoints[indexX + 1][indexY], currentX - indexX);
                    float w1 = interpolate(discretePoints[indexX][indexY + 1], discretePoints[indexX + 1][indexY + 1], currentX - indexX);
                    float w = interpolate(w0, w1, currentY - indexY);

                    map[i][k] += w;
                }
            }
        }

        normalize();
    }

    private void normalize() {
        float min = Float.MAX_VALUE;
        for(int i = 0; i < WIDTH; i++) {
            for(int k = 0; k < HEIGHT; k++) {
                if(map[i][k] < min) {
                    min = map[i][k];
                }
            }
        }

        for(int i = 0; i < WIDTH; i++) {
            for(int k = 0; k < HEIGHT; k++) {
                map[i][k] -= min;
            }
        }

        float max = Float.MIN_VALUE;
        for(int i = 0; i < WIDTH; i++) {
            for(int k = 0; k < HEIGHT; k++) {
                if(map[i][k] > max) {
                    max = map[i][k];
                }
            }
        }

        for(int i = 0; i < WIDTH; i++) {
            for(int k = 0; k < HEIGHT; k++) {
                map[i][k] /= max;
            }
        }
    }

    private float interpolate(float a, float b, float t) {
        float t2 = (1 - MathUtil.cos(t * MathUtil.PI)) / 2;
        return (a * (1 - t2) + b * t2);
    }

    public static void main(String[] args) {
        while(true) {
            int w = 128;
            int h = 128;

            LevelGenerator vn = new LevelGenerator(w, h);
            vn.startFrequencyX = 10;
            vn.startFrequencyY = 10;
            vn.calculate();
            float[][] map = vn.map;

            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            int[] pixels = new int[w * h];
            for(int y = 0; y < h; y++) {
                for(int x = 0; x < w; x++) {
                    int i = x + y * w;

                    pixels[i] = map[x][y] > 0.5 ? 0x208020 : 0x000080;
                }
            }
            img.setRGB(0, 0, w, h, pixels, 0, w);

            JOptionPane.showMessageDialog(null, null, "Another", JOptionPane.YES_NO_OPTION, new ImageIcon(img.getScaledInstance(w * 4, h * 4, Image.SCALE_AREA_AVERAGING)));
        }
    }

}
