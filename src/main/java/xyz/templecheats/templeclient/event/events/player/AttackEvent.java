package xyz.templecheats.templeclient.event.events.player;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Event;

public abstract class AttackEvent extends Event {
    private final Entity entity;

    public AttackEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public static class Pre extends AttackEvent {
        public Pre(Entity entity) {
            super(entity);
        }
    }

    public static class Post extends AttackEvent {
        public Post(Entity entity) {
            super(entity);
        }
    }
}
