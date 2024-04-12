package xyz.templecheats.templeclient.event.events.render;

import net.minecraft.entity.Entity;
import xyz.templecheats.templeclient.event.EventCancellable;

public class NamePlateEvent extends EventCancellable {
    public final Entity entity;

    public NamePlateEvent(final Entity entity) {
        this.entity = entity;
    }
}
