package xyz.templecheats.templeclient.features.module.modules.render.esp.sub;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.render.Render3DPostEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.math.MathUtil;
import xyz.templecheats.templeclient.util.render.animation.Easing;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;
import xyz.templecheats.templeclient.util.time.TimerUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.lwjgl.opengl.GL11.*;
import static xyz.templecheats.templeclient.util.render.RenderUtil.drawGradientCircleOutline;

public class Spawn extends Module {

    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final IntSetting range = new IntSetting("Range", this, 0, 30, 12);
    private final DoubleSetting lineWidth = new DoubleSetting("Line Width", this, 0.1, 5, 1);
    private final DoubleSetting radius = new DoubleSetting("Radius", this, 0.0, 6.0, 2.0);
    private final DoubleSetting delay = new DoubleSetting("Delay", this, 0, 10000, 300);
    private final DoubleSetting globalsLength = new DoubleSetting("Length", this, 0, 10000, 100);
    private final IntSetting alpha = new IntSetting("Alpha", this, 0, 255, 255);
    private final IntSetting points = new IntSetting("Points", this, 1, 100, 20);
    private final DoubleSetting rotateSpeed = new DoubleSetting("Rotate Speed", this, 0.0, 10.0, 2.0);
    private final IntSetting interval = new IntSetting("Interval", this, 1, 100, 2);
    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", this, Mode.Normal);

    /****************************************************************
     *                      Extension Settings
     ****************************************************************/
    private final BooleanSetting extension = new BooleanSetting("Extension", this, true);
    private final EnumSetting<Easing> extensionEasing = new EnumSetting<>("E-Easing", this, Easing.Linear);
    private final DoubleSetting extensionLength = new DoubleSetting("E-Length", this, 100, 5000, 1000);
    private final DoubleSetting extensionRadius = new DoubleSetting("E-Radius", this, 0.0, 6.0, 2.0);

    /****************************************************************
     *                     Rising Settings
     ****************************************************************/
    private final BooleanSetting rising = new BooleanSetting("Rising", this, true);
    private final EnumSetting<Easing> risingEasing = new EnumSetting<>("R-Easing", this, Easing.Linear);
    private final DoubleSetting risingLength = new DoubleSetting("R-Length", this, 100, 5000, 1000);
    private final DoubleSetting risingHeight = new DoubleSetting("R-Height", this, -2.0, 2.0, 1.0);

    /****************************************************************
     *                      Variables
     ****************************************************************/
    private final ConcurrentHashMap<EntityEnderCrystal, Long> crystals = new ConcurrentHashMap<>();
    private final TimerUtil timer = new TimerUtil();

    public Spawn() {
        super("Spawn", "Render circle around crystal when placed", Keyboard.KEY_NONE, Category.Render, true);
        registerSettings(
                extension, extensionLength, extensionRadius, extensionEasing,
                rising, risingLength, risingHeight, risingEasing,
                range, lineWidth, radius, delay, globalsLength, alpha, points, rotateSpeed, interval, mode
        );
    }


    @Override
    public void onEnable() {
        reset();
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) {
            reset();
        }
    }

    @Listener
    public void onRender3D(Render3DPostEvent event) {
        if (mc.player == null || mc.world == null) return;

        for (Entity entity : mc.world.loadedEntityList) {
            if (!(entity instanceof EntityEnderCrystal)) continue;
            if (mc.player.getDistance(entity) <= range.intValue() && !crystals.containsKey(entity)) {
                crystals.put((EntityEnderCrystal) entity, System.currentTimeMillis());
            }
        }

        switch (mode.value()) {
            case Normal:
                for (Map.Entry<EntityEnderCrystal, Long> entry : crystals.entrySet()) {
                    EntityEnderCrystal centre = entry.getKey();
                    long timestamp = entry.getValue();
                    draw(centre, timestamp);
                }
                break;

            case New:
                int time = 0;
                for (int i = 1; i <= points.intValue(); i++) {
                    if (timer.passedMs((long) delay.floatValue())) {
                        for (Map.Entry<EntityEnderCrystal, Long> entry : crystals.entrySet()) {
                            EntityEnderCrystal centre = entry.getKey();
                            long timestamp = entry.getValue();
                            draw(centre, timestamp - time);
                        }
                        time += interval.intValue();
                        timer.reset();
                    }
                }
                break;
        }
    }

    public void draw(EntityEnderCrystal entity, Long timeStamp) {
        double globalsLen = globalsLength.floatValue();
        long firstLength = extension.booleanValue() ? (long) extensionLength.floatValue() : (long) globalsLen;
        long secondLength = rising.booleanValue() ? (long) risingLength.floatValue() : (long) globalsLen;
        double delta = (System.currentTimeMillis() - timeStamp);

        double maxLength = MathUtil.coerceIn(globalsLen, firstLength, secondLength);

        double height = rising.booleanValue() ? risingEasing.value().inc(delta / risingLength.floatValue()) * risingHeight.floatValue() : 0;
        double extended = extension.booleanValue() ? extensionEasing.value().inc(delta / extensionLength.floatValue()) * extensionRadius.floatValue() : radius.floatValue() * (delta / globalsLen);

        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.getRenderPartialTicks() - mc.getRenderManager().viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.getRenderPartialTicks() - mc.getRenderManager().viewerPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.getRenderPartialTicks() - mc.getRenderManager().viewerPosZ;

        GL11.glPushMatrix();
        setup();
        GlStateManager.translate(0, y + height, 0);
        GlStateManager.translate(x, 0.1, z);
        GlStateManager.rotate((mc.player.ticksExisted + mc.getRenderPartialTicks()) * -rotateSpeed.floatValue(), 0.0F, 1.0F, 0.0F);

        if (delta <= maxLength) {
            drawGradientCircleOutline((float) extended, lineWidth.floatValue(), (int) (alpha.intValue() * (1 - delta / maxLength)));
        }

        release();
        GL11.glPopMatrix();
    }

    private void reset() {
        crystals.clear();
    }

    private void setup() {
        glDepthMask(false);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();
        GlStateManager.shadeModel(GL_SMOOTH);
        GlStateManager.disableCull();
    }

    private void release() {
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        glDepthMask(true);
        glDisable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        GlStateManager.shadeModel(GL11.GL_FLAT);
    }

    private enum Mode {
        Normal, New
    }
}
