package xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items;

import java.io.IOException;
import java.util.ArrayList;

public class Item {
    private final String label;
    protected float x, y;
    protected int width, height;
    public final ArrayList<Item> items = new ArrayList<>();

    public Item(String label) {
        this.label = label;
    }

    public Item(String label, int x, int y) {
        this.label = label;
        this.x = x;
        this.y = y;
    }

    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    }

    public void drawScreenPost(int mouseX, int mouseY) {
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
    }

    public final String getLabel() {
        return this.label;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    protected boolean isHovering(final int mouseX, final int mouseY) {
        return mouseX >= this.getX() &&
                mouseX <= this.getX() + this.getWidth() &&
                mouseY >= this.getY() &&
                mouseY <= this.getY() + this.height;
    }
}

