package xyz.templecheats.templeclient.Module.COMBAT;

import xyz.templecheats.templeclient.Module.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class HitBox extends Module {
    public HitBox() {
        super("HitBox", Keyboard.KEY_NONE, Category.COMBAT);
    }

    @SubscribeEvent
    public void onUpdate(RenderWorldLastEvent e) {
        if (this.toggled) {
            float size = 1.0f; // Set the size to a fixed value

            for (EntityPlayer player : mc.world.playerEntities) {
                if (player != null && player != mc.player) {
                    player.setEntityBoundingBox(new AxisAlignedBB(
                            player.posX - size,
                            player.getEntityBoundingBox().minY,
                            player.posZ - size,
                            player.posX + size,
                            player.getEntityBoundingBox().maxY,
                            player.posZ + size
                    ));
                }
            }
        } else {
            for (EntityPlayer player : mc.world.playerEntities) {
                if (player != null && player != mc.player) {
                    player.setEntityBoundingBox(new AxisAlignedBB(
                            player.posX - 0.3F,
                            player.getEntityBoundingBox().minY,
                            player.posZ - 0.3F,
                            player.posX + 0.3F,
                            player.getEntityBoundingBox().maxY,
                            player.posZ + 0.3F
                    ));
                }
            }
        }
    }

    @Override
    public void onDisable() {}
}
