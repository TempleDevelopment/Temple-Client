package xyz.templecheats.templeclient.features.gui.clickgui.particles;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import java.util.Random;

public class Snow {

    private int x;
    private int y;
    private int speed;
    private int size;

    public Snow(int x, int y, int speed, int size) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.size = size;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void drawSnow(ScaledResolution res) {
        Gui.drawRect(getX(), getY(), getX() + size, getY() + size, 0x99C9C5C5);

        setY(getY() + speed);

        if (getY() > res.getScaledHeight() + 10 || getY() < -10) {
            Random random = new Random();
            setY(-10);
            setX(random.nextInt(res.getScaledWidth()));
            speed = random.nextInt(3) + 1;
            size = random.nextInt(2) + 1;
        }
    }
}