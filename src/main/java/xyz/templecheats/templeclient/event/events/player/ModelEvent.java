package xyz.templecheats.templeclient.event.events.player;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import xyz.templecheats.templeclient.event.EventCancellable;

public class ModelEvent extends EventCancellable {
    private final ModelBase modelBase;
    private final Entity entity;
    private final float limbSwing;
    private final float limbSwingAmount;
    private final float ageInTicks;
    private final float netHeadYaw;
    private final float headPitch;
    private final float scale;

    public ModelEvent(ModelBase modelBase, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.modelBase = modelBase;
        this.entity = entity;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.netHeadYaw = netHeadYaw;
        this.headPitch = headPitch;
        this.scale = scale;
    }

    public ModelBase getModelBase() {
        return modelBase;
    }

    public Entity getEntity() {
        return entity;
    }

    public float getLimbSwing() {
        return limbSwing;
    }

    public float getLimbSwingAmount() {
        return limbSwingAmount;
    }

    public float getAgeInTicks() {
        return ageInTicks;
    }

    public float getNetHeadYaw() {
        return netHeadYaw;
    }

    public float getHeadPitch() {
        return headPitch;
    }

    public float getScale() {
        return scale;
    }
}
