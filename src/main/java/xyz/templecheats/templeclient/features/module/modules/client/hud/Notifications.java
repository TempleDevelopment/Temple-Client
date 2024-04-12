package xyz.templecheats.templeclient.features.module.modules.client.hud;

import net.minecraft.client.gui.ScaledResolution;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;

public class Notifications extends HUD.HudElement {
    /*
     * Variables
     */
    private static String notificationText = "";
    private static long notificationTime;
    private float notificationFade;

    public Notifications() {
        super("Notifications", "Shows notifications on the screen");
    }

    @Override
    public void renderElement(ScaledResolution sr) {
        updateFade();
        if (notificationFade > 0) {
            drawNotification(sr);
        }
    }

    public static void showNotification(String text) {
        notificationText = text;
        notificationTime = System.currentTimeMillis();
    }

    private void updateFade() {
        long timeSinceNotification = System.currentTimeMillis() - notificationTime;
        if (timeSinceNotification < 500) {
            notificationFade = Math.min(1.0f, timeSinceNotification / 500.0f);
        } else if (timeSinceNotification < 1500) {
            notificationFade = 1.0f;
        } else {
            notificationFade = Math.max(0.0f, 1.0f - (timeSinceNotification - 1500) / 1000.0f);
        }
    }

    private void drawNotification(ScaledResolution sr) {
        final String notification = notificationText;
        this.setWidth(font.getStringWidth(notification));
        this.setHeight(font.getFontHeight());

        font.drawString(notification, this.getX(), this.getY(), ClickGUI.INSTANCE.getStartColor().getRGB(), true, 1.0f);
    }
}