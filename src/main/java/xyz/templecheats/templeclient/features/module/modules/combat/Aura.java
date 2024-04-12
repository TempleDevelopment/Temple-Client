package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.event.events.player.MotionEvent;
import xyz.templecheats.templeclient.event.events.render.Render3DPrePreEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.Colors;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.rotation.RotationUtil;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

import java.awt.*;
import java.util.Comparator;

import static org.lwjgl.opengl.GL11.*;

public class Aura extends Module {
    /*
     * Settings
     */
    private final BooleanSetting ignoreWalls = new BooleanSetting("Raytrace", this, true);
    private final BooleanSetting requireWeapon = new BooleanSetting("Sword-Only", this, true);
    private final BooleanSetting rotate = new BooleanSetting("Rotate", this, true);
    private final BooleanSetting renderRange = new BooleanSetting("Render Range", this, true);
    private final BooleanSetting onlyCritical = new BooleanSetting("Only Critical", this, false);
    private final BooleanSetting disableOnDeath = new BooleanSetting("Disable On Death", this, false);
    private final BooleanSetting players = new BooleanSetting("Players", this, true);
    private final BooleanSetting animals = new BooleanSetting("Animals", this, false);
    private final BooleanSetting mobs = new BooleanSetting("Mobs", this, false);
    private final IntSetting waitTick = new IntSetting("Wait Tick", this, 0, 20, 0);
    private final DoubleSetting range = new DoubleSetting("Range", this, 0.1d, 6d, 4.5d);
    private final DoubleSetting renderSpeed = new DoubleSetting("Render Speed", this, 0.1, 5, 2.5);
    private final EnumSetting<WaitMode> waitMode = new EnumSetting<>("Mode", this, WaitMode.Dynamic);

    /*
     * Variables
     */
    private int waitCounter;
    private EntityLivingBase target, renderTarget = null;

    public Aura() {
        super("Aura","Automatically attack entities nearby", Keyboard.KEY_NONE, Category.Combat);

        registerSettings(ignoreWalls, onlyCritical, requireWeapon, disableOnDeath, players, mobs, animals, waitTick, range, renderRange, renderSpeed, waitMode);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        waitCounter = 0;
    }

    @Listener
    public void onMotion(MotionEvent event) {
        if (disableOnDeath.booleanValue() && mc.player.getHealth() <= 0) {
            this.disable();
            return;
        }

        switch (event.getStage()) {

            case PRE:
                if (waitMode.value() == WaitMode.Dynamic) {
                    if (mc.player.getCooledAttackStrength(0) < 1) {
                        return;
                    }
                } else if (waitTick.intValue() > 0) {
                    if (waitCounter < waitTick.intValue()) {
                        waitCounter++;
                        return;
                    } else {
                        waitCounter = 0;
                    }
                }
                if (requireWeapon.booleanValue() && !hasSwordMainHand()) return;
                this.target = mc.world.loadedEntityList.stream()
                        .filter(entity -> entity instanceof EntityLivingBase && !entity.equals(mc.player) && (!(entity instanceof EntityOtherPlayerMP) || !((EntityOtherPlayerMP) entity).getGameProfile().equals(mc.player.getGameProfile())))
                        .map(entity -> (EntityLivingBase) entity)
                        .filter(entity -> {
                            double distance = entity.getDistance(mc.player);
                            return distance <= range.doubleValue() && ((players.booleanValue() && entity instanceof EntityPlayer && !TempleClient.friendManager.isFriend(entity.getName()))
                                    || (animals.booleanValue() && entity instanceof EntityAnimal)
                                    || (mobs.booleanValue() && !(entity instanceof EntityPlayer) && !(entity instanceof EntityAnimal)))
                                    && (!ignoreWalls.booleanValue() || mc.player.canEntityBeSeen(entity));
                        })
                        .min(Comparator.comparing(entity -> entity.getDistance(mc.player)))
                        .orElse(null);

                this.renderTarget = this.target;

                if(this.target == null) {
                    return;
                }

                if(mc.player.onGround && onlyCritical.booleanValue()) {
                    mc.player.motionY = 0.15;
                }

                if(this.rotate.booleanValue()) {
                    final float[] rotations = RotationUtil.getRotations(this.target);
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

    private boolean hasSwordMainHand() {
        return mc.player.getHeldItemMainhand().getItem() instanceof ItemSword;
    }
    @Override
    public String getHudInfo() {
        if(this.renderTarget != null) {
            return this.renderTarget.getName();
        }

        return "";
    }
    private float i = 0.0f;
    @Listener
    public void onRender3DPre(Render3DPrePreEvent event) {
        if (renderTarget != null && renderRange.booleanValue() && hasSwordMainHand()) {
            final Vec3d vec = RenderUtil.interpolateEntity(renderTarget);
            final Color color = Colors.INSTANCE.getGradient()[0];
            final Color color2 = Colors.INSTANCE.getGradient()[1];
            final Color top = new Color(color2.getRed(), color2.getGreen(), color2.getBlue(), 0);
            final float sin = ((float) Math.sin(i / (100.0f / renderSpeed.doubleValue())) / 2.0f);
            final float sin2 = ((float) Math.sin(i / 25.0f + renderSpeed.floatValue() / 2.0f));
            i++;
            glPushMatrix();
            glEnable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glDisable(GL_DEPTH_TEST);
            glShadeModel(GL_SMOOTH);
            glDisable(GL_CULL_FACE);
            glBegin(GL_QUAD_STRIP);
            for (int i = 0; i <= 360; i++) {
                final double x = ((Math.cos(i * Math.PI / 180F) * range.doubleValue()) + vec.x);
                final double y = (vec.y + (renderTarget.height / 2.0f));
                final double z = ((Math.sin(i * Math.PI / 180F) * range.doubleValue()) + vec.z);
                RenderUtil.glColor(color);
                glVertex3d(x, y + (sin2 * renderTarget.height), z);
                RenderUtil.glColor(top);
                glVertex3d(x, y + (sin * renderTarget.height), z);
            }

            glEnd();
            glEnable(GL_CULL_FACE);
            glShadeModel(GL_FLAT);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);
            glPopMatrix();
        }
    }

    private enum WaitMode {
        Dynamic,
        Static
    }
}