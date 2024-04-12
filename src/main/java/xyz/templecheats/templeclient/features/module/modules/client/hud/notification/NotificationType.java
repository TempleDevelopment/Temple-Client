package xyz.templecheats.templeclient.features.module.modules.client.hud.notification;

public enum NotificationType {
    SUCCESS("Success"),
    INFO("Info"),
    ERROR("Error"),
    DEBUG("Debug");

    public final String typeName;

    NotificationType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}



