package xyz.templecheats.templeclient.features.module.modules.client.hud.notification;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import xyz.templecheats.templeclient.features.gui.font.CFont;
import xyz.templecheats.templeclient.features.gui.font.TempleIcon;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;
import xyz.templecheats.templeclient.util.color.impl.RectBuilder;
import xyz.templecheats.templeclient.util.math.Vec2d;
import xyz.templecheats.templeclient.util.render.Easing;
import xyz.templecheats.templeclient.util.setting.impl.ColorSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

import java.awt.*;
import java.util.*;
import java.util.List;

import static xyz.templecheats.templeclient.features.gui.font.Fonts.*;
import static xyz.templecheats.templeclient.util.math.MathUtil.lerp;

public class Notifications extends HUD.HudElement {
    public static Notifications INSTANCE;
    /*
     * Settings
     */
    public final EnumSetting<Easing> easing = new EnumSetting <>("Easing", this, Easing.Linear);
    public final DoubleSetting keepTime = new DoubleSetting("KeepTime", this, 1, 5, 2);
    public final DoubleSetting showTime = new DoubleSetting("ShowTime", this, 1, 5, 3);
    public final DoubleSetting hideTime = new DoubleSetting("HieTime", this, 5, 10, 10);
    /*
     * Variables
     */
    private static final List<NotificationInfo> notificationList = new ArrayList<>();
    private double initialX;

    public Notifications() {
        super("Notifications", "Shows notifications on the screen");
        INSTANCE = this;
        registerSettings(fill, outline, blur, color, outlineColor, outlineWidth, blurRadius, keepTime, showTime, hideTime, easing);
    }

    public static void addMessage(String text, String description, NotificationType type) {
        while (notificationList.size() >= 6) {
            notificationList.remove(0);
        }
        notificationList.add(new NotificationInfo(text, description, type));
    }

    @Override
    public void renderElement(ScaledResolution sr) {
        double y = getY();

        notificationList.removeIf(NotificationInfo::shouldRemove);

        if (!notificationList.isEmpty()) {
            initialX = getX();
        }

        Iterator <NotificationInfo> iterator = notificationList.iterator();
        while (iterator.hasNext()) {
            NotificationInfo notification = iterator.next();
            double progress = notification.getProgress();
            double offsetX = lerp(initialX + sr.getScaledWidth(), initialX, Math.pow(progress, 2.3));

            draw(new Vec2d(offsetX, y), notification.text, notification.description, notification.type);

            y -= progress * (getHeight() + 10.0);

            this.setWidth(font18.getStringWidth(notification.description) + 30);
            this.setHeight(25);

            // Remove the notification if it should be removed
            if (notification.shouldRemove()) {
                iterator.remove();
            }
        }
    }

    private void draw(Vec2d pos, String text, String description, NotificationType type) {
        float textWidth = font18.getStringWidth(description) + 30;
        new RectBuilder(new Vec2d(pos.x, pos.y), new Vec2d(pos.x + textWidth, pos.y + getHeight()))
                .outlineColor(outlineColor.getColor())
                .width(outline.booleanValue() ? outlineWidth.doubleValue() : 0)
                .color(fill.booleanValue() ? color.getColor() : new Color(0,0,0,0))
                .radius(2.0)
                .blur(blur.booleanValue() ? blurRadius.doubleValue() : 0)
                .drawBlur()
                .draw();

        icon46.drawIcon(type.getIcon().getIcon(), (float) (pos.x - 5), (float) (pos.y + 4), Color.WHITE, false);
        font20.drawString(text, pos.x + 25, pos.y + 4, ClickGUI.INSTANCE.getClientColor(0), false);
        font18.drawString(description, pos.x + 25, pos.y + font20.getFontHeight() + 5, new Color(200, 200, 200, 255).getRGB(), false);
    }
}
