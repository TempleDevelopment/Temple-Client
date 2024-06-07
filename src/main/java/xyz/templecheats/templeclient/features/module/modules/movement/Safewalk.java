package xyz.templecheats.templeclient.features.module.modules.movement;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.player.MoveEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.render.Freecam;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;

public class Safewalk extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final BooleanSetting slabs = new BooleanSetting("Slabs", this, true);

    /****************************************************************
     *                      Variables
     ****************************************************************/
    Minecraft mc = Minecraft.getMinecraft();
    public static Safewalk INSTANCE;

    public Safewalk() {
        super("Safewalk", "Prevents you from falling off blocks", Keyboard.KEY_NONE, Category.Movement);
        INSTANCE = this;
        registerSettings(slabs);
    }

    @Listener
    public void safeWalk(MoveEvent event) {
        if (Freecam.isFreecamActive()) {
            return;
        }

        final double yOffset = slabs.booleanValue() ? -0.5D : -1D;

        double x = event.x;
        double y = event.y;
        double z = event.z;

        if (mc.player.onGround) {
            double increment;
            for (increment = 0.05D; x != 0.0D && isOffsetBBEmpty(x, yOffset, 0.0D); ) {
                if (x < increment && x >= -increment) {
                    x = 0.0D;
                } else if (x > 0.0D) {
                    x -= increment;
                } else {
                    x += increment;
                }
            }
            for (; z != 0.0D && isOffsetBBEmpty(0.0D, yOffset, z); ) {
                if (z < increment && z >= -increment) {
                    z = 0.0D;
                } else if (z > 0.0D) {
                    z -= increment;
                } else {
                    z += increment;
                }
            }
            for (; x != 0.0D && z != 0.0D && isOffsetBBEmpty(x, yOffset, z); ) {
                if (x < increment && x >= -increment) {
                    x = 0.0D;
                } else if (x > 0.0D) {
                    x -= increment;
                } else {
                    x += increment;
                }
                if (z < increment && z >= -increment) {
                    z = 0.0D;
                } else if (z > 0.0D) {
                    z -= increment;
                } else {
                    z += increment;
                }
            }
        }

        event.x = x;
        event.y = y;
        event.z = z;
    }

    private boolean isOffsetBBEmpty(double offsetX, double offsetY, double offsetZ) {
        return mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(offsetX, offsetY, offsetZ)).isEmpty();
    }
}