package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

import java.util.Comparator;

public class BowAimbot extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final BooleanSetting players = new BooleanSetting("Players", this, true);
    private final BooleanSetting mobs = new BooleanSetting("Mobs", this, false);
    private final BooleanSetting animals = new BooleanSetting("Animals", this, false);
    private final DoubleSetting range = new DoubleSetting("Range", this, 1, 25, 10);
    private final IntSetting extrapolation = new IntSetting("Extrapolation", this, 0, 10, 1);

    public BowAimbot() {
        super("BowAimbot", "Automatically aims your bow at entities", Keyboard.KEY_NONE, Category.Combat);
        registerSettings(players, mobs, animals, range, extrapolation);
    }

    @Override
    public void onUpdate() {
        Entity target = mc.world.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityLivingBase)
                .filter(entity -> (entity instanceof EntityPlayer && players.booleanValue()) ||
                        (entity.isCreatureType(EnumCreatureType.MONSTER, false) && mobs.booleanValue()) ||
                        (entity instanceof IAnimals && animals.booleanValue() && !entity.isCreatureType(EnumCreatureType.MONSTER, false)))
                .filter(entity -> entity != mc.player && !TempleClient.friendManager.isFriend(entity.getName()))
                .filter(entity -> mc.player.getDistance(entity) <= range.doubleValue())
                .min(Comparator.comparing(entity -> entity.getDistance(mc.player)))
                .orElse(null);

        if (target != null) {
            aimAtTarget((EntityLivingBase) target);
        }
    }

    private void aimAtTarget(EntityLivingBase target) {
        if (mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemBow || mc.player.getHeldItemOffhand().getItem() instanceof net.minecraft.item.ItemBow) {
            double speedX = target.posX - target.lastTickPosX;
            double speedY = target.posY - target.lastTickPosY;
            double speedZ = target.posZ - target.lastTickPosZ;

            double accelerationX = speedX - (target.lastTickPosX - target.prevPosX);
            double accelerationY = speedY - (target.lastTickPosY - target.prevPosY);
            double accelerationZ = speedZ - (target.lastTickPosZ - target.prevPosZ);

            double posX = target.posX + speedX + 0.5 * accelerationX;
            double posZ = target.posZ + speedZ + 0.5 * accelerationZ;

            double diffX = posX - mc.player.posX;
            double diffZ = posZ - mc.player.posZ;

            mc.player.rotationYaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0F;
        }
    }
}