package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.module.Module;

import java.util.Comparator;

public class AimAssist extends Module {
    private EntityLivingBase renderTarget;
    public AimAssist() {
        super("AimAssist","Locks on target", Keyboard.KEY_NONE, Category.Combat);
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

        if (target != null && mc.player.canEntityBeSeen(target)) {
            mc.player.rotationYaw = rotations(target)[0];
            mc.player.rotationPitch = rotations(target)[1];
        }
    }

    public float[] rotations(EntityPlayer entity) {
        double x = entity.posX - mc.player.posX;
        double y = entity.posY - (mc.player.posY + mc.player.getEyeHeight()) + 1.5;
        double z = entity.posZ - mc.player.posZ;

        double u = MathHelper.sqrt(x * x + z * z);

        float u2 = (float) (MathHelper.atan2(z, x) * (180D / Math.PI) - 90.0F);
        float u3 = (float) (-MathHelper.atan2(y, u) * (180D / Math.PI));

        return new float[]{u2, u3};
    }
    @Override
    public String getHudInfo() {
        if(this.renderTarget != null) {
            return this.renderTarget.getName();
        }

        return "";
    }
}