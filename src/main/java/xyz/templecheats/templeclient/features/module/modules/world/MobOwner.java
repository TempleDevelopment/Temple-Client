package xyz.templecheats.templeclient.features.module.modules.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;

import java.util.UUID;

import static xyz.templecheats.templeclient.util.math.MathUtil.round;

public class MobOwner extends Module {

    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final BooleanSetting speed = new BooleanSetting("Speed", this, true);
    private final BooleanSetting jump = new BooleanSetting("Jump", this, true);

    public MobOwner() {
        super("MobOwner", "Tells you the identity of who tamed a mob", Keyboard.KEY_NONE, Category.World);
        registerSettings(speed, jump);
    }

    @Override
    public void onUpdate() {
        if (mc.world == null) {
            return;
        }

        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityTameable) {
                final EntityTameable entityTameable = (EntityTameable) entity;
                if (entityTameable.isTamed() && entityTameable.getOwner() != null) {
                    entityTameable.setAlwaysRenderNameTag(true);
                    entityTameable.setCustomNameTag("Owner: " + entityTameable.getOwner().getDisplayName().getFormattedText());
                }
            }
            if (!(entity instanceof AbstractHorse)) {
                continue;
            }
            UUID uuid = ((AbstractHorse) entity).getOwnerUniqueId();
            String owner = "";
            if (uuid == null) {
                entity.setCustomNameTag("Not tamed");
            } else
                try {
                    owner = String.valueOf(mc.world.getPlayerEntityByUUID(uuid));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            entity.setCustomNameTag("Owner: " + owner + getSpeed((AbstractHorse) entity) + getJump((AbstractHorse) entity));
        }
    }

    @Override
    public void onDisable() {
        for (final Entity entity : mc.world.loadedEntityList) {
            if (!(entity instanceof EntityTameable)) {
                if (!(entity instanceof AbstractHorse)) {
                    continue;
                }
            }
            try {
                entity.setAlwaysRenderNameTag(false);
                System.out.println("remove");
            } catch (Exception ignored) {
            }
        }
    }

    private String getSpeed(AbstractHorse horse) {
        return !speed.booleanValue() ? "" : " S: " + horse.getAIMoveSpeed();
    }

    private String getJump(AbstractHorse horse) {
        return !jump.booleanValue() ? "" : " J: " + round(-0.1817584952 * Math.pow(horse.getHorseJumpStrength(), 3) + 3.689713992 * Math.pow(horse.getHorseJumpStrength(), 2) + 2.128599134 * horse.getHorseJumpStrength() - 0.343930367, 2);
    }
}