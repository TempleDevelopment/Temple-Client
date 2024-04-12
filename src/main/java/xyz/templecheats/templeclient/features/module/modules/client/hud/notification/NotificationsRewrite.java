package xyz.templecheats.templeclient.features.module.modules.client.hud.notification;

import net.minecraft.client.gui.ScaledResolution;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;
import xyz.templecheats.templeclient.util.color.impl.RectBuilder;
import xyz.templecheats.templeclient.util.math.Vec2d;
import xyz.templecheats.templeclient.util.render.Easing;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationsRewrite extends HUD.HudElement {
    public static NotificationsRewrite INSTANCE;
    /*
     * Settings
     */
    public final EnumSetting<Easing> easing = new EnumSetting <>("Easing", this, Easing.IN_CUBIC);
    public final DoubleSetting keepTime = new DoubleSetting("KeepTime", this, 1, 5, 2);
    public final DoubleSetting showTime = new DoubleSetting("ShowTime", this, 1, 5, 3);
    public final DoubleSetting hideTime = new DoubleSetting("HieTime", this, 1, 5, 5);
    /*
     * Variables
     */
    private static final List<NotificationInfo> notificationList = new ArrayList<>();
    private double initialX;

    public NotificationsRewrite() {
        super("Notifications2", "Shows notifications on the screen");
        INSTANCE = this;
        registerSettings(easing, keepTime, showTime, hideTime);
    }

    public static void addMessage(String text, String description, NotificationType type) {
        notificationList.add(new NotificationInfo(text, description, type));
    }

    @Override
    public void renderElement(ScaledResolution sr) {
        double y = getY();

        notificationList.removeIf(NotificationInfo::shouldRemove);
        while (notificationList.size() > 5) {
            notificationList.remove(0);
        }
        if (!notificationList.isEmpty()) {
            initialX = getX();
        }
        for (NotificationInfo notification : notificationList) {
            draw(new Vec2d(initialX, y), notification.text, notification.description);

            y -= notification.getProgress() * (getHeight() + 10.0);

            this.setWidth(font.getStringWidth(notification.text + notification.description));
            this.setHeight(25);

        }
    }

    private void draw(Vec2d pos, String text, String description) {
        float textWidth = font.getStringWidth(text + description);
        new RectBuilder(new Vec2d(pos.x, pos.y), new Vec2d(pos.x + textWidth, pos.y + getHeight()))
                .color(new Color(255, 255, 255, 150))
                .radius(2.0)
                .draw();

        font.drawString(text + description, (float) pos.x, (float) (pos.y + getHeight() / 2.5), new Color(0, 0, 0, 255), false, 1.0f);
    }
}
