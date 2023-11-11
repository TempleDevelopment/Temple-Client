package xyz.templecheats.templeclient.gui.clickgui;

public class HoverUtils {
    public static boolean hovered(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX > x && mouseY > y && mouseX < width && mouseY < height;
    }
}
