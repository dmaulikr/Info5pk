package com.florianwoelki.info5pk.level.tile;

import com.florianwoelki.info5pk.level.Level;

import java.awt.*;

/**
 * Created by Florian Woelki on 19.11.16.
 */
public class WaterTile extends Tile {

    public WaterTile(int id) {
        super( id );
    }

    @Override
    public void render(Graphics g, Level level, int x, int y) {
        // int color = level.waterColor;

        g.setColor( Color.BLUE );
        g.fillRect( x * 16, y * 16, 16, 16 );
    }

}