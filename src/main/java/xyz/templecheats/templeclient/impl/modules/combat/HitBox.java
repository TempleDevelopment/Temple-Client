package xyz.templecheats.templeclient.impl.modules.combat;

import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.impl.modules.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.api.setting.Setting;

public class Hitbox extends Module {
    public Hitbox() {
        super("Hitbox","Increases entities hitbox", Keyboard.KEY_NONE, Category.Combat);

        TempleClient.settingsManager.rSetting(new Setting("Size", this, 1, 0.1, 1, false));
    }

    @SubscribeEvent
    public void onUpdate(RenderWorldLastEvent e) {
        if (this.toggled) {
            float size = (float) TempleClient.settingsManager.getSettingByName(this.getName(), "Size").getValDouble();

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
