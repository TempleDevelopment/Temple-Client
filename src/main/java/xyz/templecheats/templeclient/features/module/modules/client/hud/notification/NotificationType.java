package xyz.templecheats.templeclient.features.module.modules.client.hud.notification;

import xyz.templecheats.templeclient.features.gui.font.TempleIcon;

public enum NotificationType {
    SUCCESS(TempleIcon.SUCCESS),
    INFO(TempleIcon.INFO),
    WARNING(TempleIcon.WARNING),
    ERROR(TempleIcon.ERROR);

    private final TempleIcon icon;

    NotificationType(TempleIcon icon) {
        this.icon = icon;
    }

    public TempleIcon getIcon() {
        return icon;
    }
}




