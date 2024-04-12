package xyz.templecheats.templeclient.features.module.modules.client.hud.notification;

import static net.minecraft.util.math.MathHelper.clamp;
import static xyz.templecheats.templeclient.features.module.modules.client.hud.notification.NotificationsRewrite.*;

public class NotificationInfo {
    String text;
    String description;
    NotificationType type;
    long spawnTime;

    private final long KEEPTIME = (long) (INSTANCE.keepTime.doubleValue() * 1000L);
    private final long SHOWTIME = (long) (INSTANCE.showTime.doubleValue() * 100L);
    private final long HIDETIME = (long) (INSTANCE.hideTime.doubleValue() * 100L);

    public NotificationInfo(String text, String description, NotificationType type) {
        this.text = text;
        this.description = description;
        this.type = type;
        this.spawnTime = System.currentTimeMillis();
    }

    private long timeExisted() {
        return System.currentTimeMillis() - spawnTime;
    }

    double getProgress() {
        double p = 0.0;

        if (timeExisted() <= SHOWTIME) p = (timeExisted() / (double) SHOWTIME);
        if (timeExisted() > SHOWTIME) p = 1.0;
        if (timeExisted() > SHOWTIME + KEEPTIME) p = 1.0 - ((timeExisted() - SHOWTIME - KEEPTIME) / (double) HIDETIME);

        return INSTANCE.easing.value().inc(clamp(p, 0.0, 1.0));
    }

    boolean shouldRemove() {
        return timeExisted() > SHOWTIME + KEEPTIME + HIDETIME + 100L;
    }
}
