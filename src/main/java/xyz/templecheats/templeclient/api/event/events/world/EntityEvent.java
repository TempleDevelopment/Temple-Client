package xyz.templecheats.templeclient.api.event.events.world;

import net.minecraft.entity.Entity;
import xyz.templecheats.templeclient.api.event.EventStageable;

public class EntityEvent extends EventStageable {
    private final Entity entity;
    
    public EntityEvent(Entity entity) {
        this.entity = entity;
    }
    
    public Entity getEntity() {
        return entity;
    }
    
    public static class Add extends EntityEvent {
        public Add(Entity entity) {
            super(entity);
        }
    }
    
    public static class Delete extends EntityEvent {
        public Delete(Entity entity) {
            super(entity);
        }
    }
}
