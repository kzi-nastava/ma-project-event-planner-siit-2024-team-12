package com.example.eventplanner.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class CustomCircleDrawable extends Drawable {

    private final Paint paint;

    public CustomCircleDrawable(int color) {
        paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas canvas) {
        int radius = Math.min(getBounds().width(), getBounds().height()) / 2;
        int cx = getBounds().centerX();
        int cy = getBounds().centerY();
        canvas.drawCircle(cx, cy, radius, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(android.graphics.ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return android.graphics.PixelFormat.TRANSLUCENT;
    }
}
