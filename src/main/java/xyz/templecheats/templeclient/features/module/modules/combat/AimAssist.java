package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;

import java.util.Comparator;

public class AimAssist extends Module {
    /*
     * Settings
     */
    private final BooleanSetting visibility = new BooleanSetting("Visible-Only", this, true);
    private final DoubleSetting smoothing = new DoubleSetting("Smoothing-Factor", this, 1.0f, 50.0f, 5.0f);

    /*
     * Variables
     */
    private EntityLivingBase renderTarget;
    public AimAssist() {
        super("AimAssist", "Locks on target", Keyboard.KEY_NONE, Category.Combat);
        registerSettings(smoothing, visibility);
    }


    @SubscribeEvent
    public void onUpdate(RenderWorldLastEvent e) {
        double range = 5;

        EntityPlayer target = mc.world.playerEntities.stream()
                .filter(entityPlayer -> entityPlayer != mc.player && !TempleClient.friendManager.isFriend(entityPlayer.getName()))
                .min(Comparator.comparing(entityPlayer -> entityPlayer.getDistance(mc.player)))
                .filter(entityPlayer -> entityPlayer.getDistance(mc.player) <= range)
                .orElse(null);

        this.renderTarget = target;

        if (target != null) {
            if (!mc.player.canEntityBeSeen(target) && visibility.booleanValue()) return;
            float[] targetRotations = rotations(target);
            float targetYaw = targetRotations[0];
            float targetPitch = targetRotations[1];
            mc.player.rotationYaw += (targetYaw - mc.player.rotationYaw) / smoothing.floatValue();
            mc.player.rotationPitch += (targetPitch - mc.player.rotationPitch) / smoothing.floatValue();
        }
    }

    public float[] rotations(EntityPlayer entity) {
        double x = entity.posX - mc.player.posX;
        double y = entity.posY - (mc.player.posY + mc.player.getEyeHeight()) + 1.5;
        double z = entity.posZ - mc.player.posZ;

        double u = MathHelper.sqrt(x * x + z * z);

        float u2 = (float)(MathHelper.atan2(z, x) * (180D / Math.PI) - 90.0F);
        float u3 = (float)(-MathHelper.atan2(y, u) * (180D / Math.PI));

        return new float[] {
                u2,
                u3
        };
    }
    @Override
    public String getHudInfo() {
        if (this.renderTarget != null) {
            return this.renderTarget.getName();
        }

        return "";
    }
}