package xyz.templecheats.templeclient.features.module.modules.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

import java.util.List;

public class AutoMount extends Module {
    /*
     * Settings
     */
    private final BooleanSetting horses = new BooleanSetting("Horses", this, true);
    private final BooleanSetting donkeys = new BooleanSetting("Donkeys", this, true);
    private final BooleanSetting llamas = new BooleanSetting("Llamas", this, true);
    private final BooleanSetting boats = new BooleanSetting("Boats", this, true);
    private final BooleanSetting minecarts = new BooleanSetting("Minecarts", this, true);
    private final BooleanSetting pigs = new BooleanSetting("Pigs", this, true);
    private final IntSetting range = new IntSetting("Range", this, 1, 6, 3);
    private final IntSetting delay = new IntSetting("Delay", this, 1, 10, 5);

    /*
     * Variables
     */
    private int ticks = 0;

    public AutoMount() {
        super("AutoMount", "Automatically mounts entities", Keyboard.KEY_NONE, Category.Misc);
        this.registerSettings(horses, donkeys, llamas, boats, minecarts, pigs, range, delay);
    }
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (mc.player == null || mc.world == null) {
                return;
            }

            if (mc.player.isRiding()) {
                return;
            }

            ticks++;

            if (ticks < delay.intValue()) {
                return;
            }

            ticks = 0;

            List < Entity > entities = mc.world.getEntitiesWithinAABBExcludingEntity(mc.player, mc.player.getEntityBoundingBox().grow(range.intValue()));
            for (Entity entity: entities) {
                if ((entity instanceof EntityHorse && horses.booleanValue()) ||
                        (entity instanceof EntityDonkey && donkeys.booleanValue()) ||
                        (entity instanceof EntityLlama && llamas.booleanValue()) ||
                        (entity instanceof EntityBoat && boats.booleanValue()) ||
                        (entity instanceof EntityMinecart && minecarts.booleanValue()) ||
                        (entity instanceof EntityPig && pigs.booleanValue())) {
                    mc.player.startRiding(entity);
                    break;
                }
            }
        }
    }

    @Override
    public void onDisable() {
        if (mc.player != null && mc.player.isRiding()) {
            mc.player.dismountRidingEntity();
        }
    }
}