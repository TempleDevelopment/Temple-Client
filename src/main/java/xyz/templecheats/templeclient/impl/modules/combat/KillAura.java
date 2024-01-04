package xyz.templecheats.templeclient.impl.modules.combat;

import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.impl.modules.Module;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.Setting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class KillAura extends Module {
    public KillAura() {
        super("KillAura", Keyboard.KEY_R, Category.COMBAT);

        ArrayList<String> options = new ArrayList<>();
        options.add("Rage");
        options.add("Rotation");

        TempleClient.settingsManager.rSetting(new Setting("Mode", this, options, "Mode"));
        TempleClient.settingsManager.rSetting(new Setting("Range", this, 4.2, 1, 5, false));
        TempleClient.settingsManager.rSetting(new Setting("OnlyCritical", this, false));
        TempleClient.settingsManager.rSetting(new Setting("Players", this, true));
        TempleClient.settingsManager.rSetting(new Setting("Animals", this, false));
        TempleClient.settingsManager.rSetting(new Setting("Mobs", this, false));
    }

    @SubscribeEvent
    public void onUpdate(RenderWorldLastEvent e) {
        String Mode = TempleClient.settingsManager.getSettingByName(this.name, "Mode").getValString();
        double range = TempleClient.settingsManager.getSettingByName(this.name, "Range").getValDouble();
        boolean onlyCrits = TempleClient.settingsManager.getSettingByName(this.name, "OnlyCritical").getValBoolean();
        boolean attackPlayers = TempleClient.settingsManager.getSettingByName(this.name, "Players").getValBoolean();
        boolean attackAnimals = TempleClient.settingsManager.getSettingByName(this.name, "Animals").getValBoolean();
        boolean attackMobs = TempleClient.settingsManager.getSettingByName(this.name, "Mobs").getValBoolean();

        EntityLivingBase target = mc.world.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityLivingBase && entity != mc.player)
                .map(entity -> (EntityLivingBase) entity)
                .filter(entity -> {
                    double distance = entity.getDistance(mc.player);
                    return distance <= range && ((attackPlayers && entity instanceof EntityPlayer)
                            || (attackAnimals && entity instanceof EntityAnimal)
                            || (attackMobs && !(entity instanceof EntityPlayer) && !(entity instanceof EntityAnimal)));
                })
                .min(Comparator.comparing(entity -> entity.getDistance(mc.player)))
                .orElse(null);

        if (target != null) {
            if (mc.player.onGround && onlyCrits) {
                mc.player.motionY = 0.15;
            }

            if (Objects.equals(Mode, "Rotation")) {
                mc.player.rotationYaw = rotations(target)[0];
                mc.player.rotationPitch = rotations(target)[1];
            }

            if (mc.player.getCooledAttackStrength(0) == 1) {
                mc.playerController.attackEntity(mc.player, target);
                mc.player.swingArm(EnumHand.MAIN_HAND);
                mc.player.resetCooldown();
            }
        }
    }

    public float[] rotations(EntityLivingBase entity) {
        double x = entity.posX - mc.player.posX;
        double y = entity.posY - (mc.player.posY + mc.player.getEyeHeight());
        double z = entity.posZ - mc.player.posZ;

        double u = MathHelper.sqrt(x * x + z * z);

        float u2 = (float) (MathHelper.atan2(z, x) * (180D / Math.PI) - 90.0F);
        float u3 = (float) (-MathHelper.atan2(y, u) * (180D / Math.PI));

        return new float[]{u2, u3};
    }
}
