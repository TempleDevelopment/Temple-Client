package xyz.templecheats.templeclient.impl.modules.combat;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.api.event.events.player.MotionEvent;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.Setting;
import xyz.templecheats.templeclient.impl.modules.Module;

import java.util.ArrayList;
import java.util.Comparator;

public class KillAura extends Module {
    private Setting waitMode;
    private Setting waitTick;
    private Setting ignoreWalls;
    private Setting rotate;
    private int waitCounter;
    private EntityLivingBase target;

    public KillAura() {
        super("KillAura", Keyboard.KEY_R, Category.COMBAT);

        ArrayList<String> options = new ArrayList<>();
        options.add("DYNAMIC");
        options.add("STATIC");

        TempleClient.settingsManager.rSetting(waitMode = new Setting("WaitMode", this, options, "DYNAMIC"));
        TempleClient.settingsManager.rSetting(waitTick = new Setting("WaitTick", this, 0, 0, 20, true));
        TempleClient.settingsManager.rSetting(ignoreWalls = new Setting("Ignore Walls", this, true));
        TempleClient.settingsManager.rSetting(new Setting("Range", this, 4.5, 0.1, 6, false));
        TempleClient.settingsManager.rSetting(rotate = new Setting("Rotate", this, true));
        TempleClient.settingsManager.rSetting(new Setting("OnlyCritical", this, false));
        TempleClient.settingsManager.rSetting(new Setting("Players", this, true));
        TempleClient.settingsManager.rSetting(new Setting("Animals", this, false));
        TempleClient.settingsManager.rSetting(new Setting("Mobs", this, false));
    }

    @Listener
    public void onMotion(MotionEvent event) {
        switch(event.getStage()) {
            case PRE:
                if(waitMode.getValString().equals("DYNAMIC")) {
                    if(mc.player.getCooledAttackStrength(0) < 1) {
                        return;
                    }
                } else if(waitMode.getValString().equals("STATIC") && waitTick.getValInt() > 0) {
                    if(waitCounter < waitTick.getValInt()) {
                        waitCounter++;
                        return;
                    } else {
                        waitCounter = 0;
                    }
                }

                double range = TempleClient.settingsManager.getSettingByName(this.name, "Range").getValDouble();
                boolean onlyCrits = TempleClient.settingsManager.getSettingByName(this.name, "OnlyCritical").getValBoolean();
                boolean attackPlayers = TempleClient.settingsManager.getSettingByName(this.name, "Players").getValBoolean();
                boolean attackAnimals = TempleClient.settingsManager.getSettingByName(this.name, "Animals").getValBoolean();
                boolean attackMobs = TempleClient.settingsManager.getSettingByName(this.name, "Mobs").getValBoolean();

                this.target = mc.world.loadedEntityList.stream()
                        .filter(entity -> entity instanceof EntityLivingBase && !entity.equals(mc.player))
                        .map(entity -> (EntityLivingBase) entity)
                        .filter(entity -> {
                            double distance = entity.getDistance(mc.player);
                            return distance <= range && ((attackPlayers && entity instanceof EntityPlayer)
                                    || (attackAnimals && entity instanceof EntityAnimal)
                                    || (attackMobs && !(entity instanceof EntityPlayer) && !(entity instanceof EntityAnimal)))
                                    && (!ignoreWalls.getValBoolean() || mc.player.canEntityBeSeen(entity));
                        })
                        .min(Comparator.comparing(entity -> entity.getDistance(mc.player)))
                        .orElse(null);

                if(this.target == null) {
                    return;
                }

                if(mc.player.onGround && onlyCrits) {
                    mc.player.motionY = 0.15;
                }

                if(this.rotate.getValBoolean()) {
                    final float[] rotations = getRotations(this.target);
                    event.setYaw(rotations[0]);
                    event.setPitch(rotations[1]);
                }
                break;
            case POST:
                if(this.target != null) {
                    mc.playerController.attackEntity(mc.player, this.target);
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    this.target = null;
                }
                break;
        }
    }

    public static float[] getRotations(EntityLivingBase entity) {
        double x = entity.posX - mc.player.posX;
        double y = entity.posY - (mc.player.posY + mc.player.getEyeHeight());
        double z = entity.posZ - mc.player.posZ;

        double u = MathHelper.sqrt(x * x + z * z);

        float yaw = (float) (MathHelper.atan2(z, x) * (180D / Math.PI) - 90.0F);
        float pitch = (float) (-MathHelper.atan2(y, u) * (180D / Math.PI));

        return new float[]{yaw, pitch};
    }
}