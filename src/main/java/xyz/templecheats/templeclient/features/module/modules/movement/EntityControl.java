package xyz.templecheats.templeclient.features.module.modules.movement;

import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.entity.HorseSaddledEvent;
import xyz.templecheats.templeclient.event.events.entity.SteerEntityEvent;
import xyz.templecheats.templeclient.features.module.Module;

public class EntityControl extends Module {

    public EntityControl() {
        super("EntityControl", "Ride entities without saddles and modify their speed", Keyboard.KEY_NONE, Category.Movement);
    }

    @Listener
    public void onSteerEntity(SteerEntityEvent event) {
        event.setCanceled(true);
    }

    @Listener
    public void onHorseSaddled(HorseSaddledEvent event) {
        event.setCanceled(true);
    }
}
