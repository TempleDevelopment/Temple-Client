package xyz.templecheats.templeclient.event.events.render;

import net.minecraft.util.EnumHandSide;
import xyz.templecheats.templeclient.event.EventStageable;

public class TransformSideFirstPersonEvent extends EventStageable {

    private final EnumHandSide enumHandSide;

    public TransformSideFirstPersonEvent(EnumHandSide enumHandSide) {
        this.enumHandSide = enumHandSide;
    }

    public EnumHandSide getEnumHandSide() {
        return this.enumHandSide;
    }
}