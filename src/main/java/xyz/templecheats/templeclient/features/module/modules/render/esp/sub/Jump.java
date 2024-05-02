package xyz.templecheats.templeclient.features.module.modules.render.esp.sub;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.player.JumpEvent;
import xyz.templecheats.templeclient.event.events.render.Render3DEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.sin;
import static net.minecraft.util.math.MathHelper.clamp;
import static net.minecraft.util.math.MathHelper.sqrt;
import static org.lwjgl.opengl.GL11.*;

public class Jump extends Module {

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", this, Mode.Jump);
    private final DoubleSetting radius = new DoubleSetting( "Radius", this, 0.3, 5.0, 1.5);
    private final DoubleSetting duration = new DoubleSetting( "Duration", this, 0.5, 3.0, 1.5);
    private final DoubleSetting width = new DoubleSetting( "Width", this, 0.1, 3.0, 1.5);
    private final DoubleSetting rotateSpeed = new DoubleSetting( "Rotate Speed", this, 0.1, 10.0, 2.0);
    private final IntSetting opacity = new IntSetting( "Opacity", this, 0, 255, 255);
    private final BooleanSetting glowInside = new BooleanSetting("Glow Inside", this, false);
    private final BooleanSetting glowOutside = new BooleanSetting("Glow Outside", this, false);
    private final DoubleSetting insideRadius = new DoubleSetting( "I-Radius", this, 0.3, 5.0, 1.5);
    private final DoubleSetting outsideRadius = new DoubleSetting( "O-Radius", this, 0.3, 5.0, 1.5);

    private boolean lastOnGround = false;
    private boolean hasJumped = false;
    private final List<Circle> circleList = new ArrayList<>();
    public Jump() {
        super("Jump", "Draw circle when you jump", Keyboard.KEY_NONE, Category.Render, true);
        registerSettings(glowInside, glowOutside, radius, duration, width, rotateSpeed, insideRadius, outsideRadius, opacity, mode);
    }

    @Override
    public void onEnable() {
        circleList.clear();
        lastOnGround = false;
        hasJumped = false;
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        if (mc.player.collidedVertically && mode.value() != Mode.Jump && !lastOnGround) {
            circleList.add(new Circle(mc.player.getPositionVector().add(0 , 0.06 , 0) , System.currentTimeMillis()));
        }
        if (lastOnGround && !mc.player.onGround) {
            hasJumped = false;
        }
        lastOnGround = mc.player.onGround;
        circleList.removeIf(it -> it.getProgress(duration.floatValue()) > 0.99);
    }

    @SubscribeEvent
    public void onJump(JumpEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }
        // I have no idea what wrong with this game
        Vec3d idk = Minecraft.getMinecraft().isSingleplayer() ? mc.player.getPositionVector().subtract(0 , 0.4 , 0) : mc.player.getPositionVector().add(0 , 0.06 , 0);

        if (mode.value() != Mode.Impact && !hasJumped) {
            circleList.add(new Circle(idk, System.currentTimeMillis()));
            //System.out.println("JumpEvent - x, y, z: " + mc.player.posX + " " + mc.player.posY + " " + mc.player.posZ);
            hasJumped = true;
        }
    }

    @Listener
    public void onRender3D(Render3DEvent event) {
        Collections.reverse(circleList);
        for (Circle circle : circleList) {
            double x = circle.pos.x - mc.getRenderManager().viewerPosX;
            double y = circle.pos.y - mc.getRenderManager().viewerPosY;
            double z = circle.pos.z - mc.getRenderManager().viewerPosZ;

            float progress = (float) circle.getProgress(duration.floatValue());
            float size = radius.floatValue();

            size *= (float) (sin(sqrt(progress * 3.14)) * 1.02);

            setup();
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate((mc.player.ticksExisted + mc.getRenderPartialTicks()) * -rotateSpeed.floatValue(), 0.0F, 1.0F, 0.0F);

            //System.out.println("RenderEvent - x, y, z: " + x + " " + y + " " + z);

            if (glowOutside.booleanValue()) {
                RenderUtil.drawFadeGradientCircleOutline(radius.floatValue() * progress , size * insideRadius.floatValue() * 0.2f, (int) (opacity.intValue() * (1 - progress)));
            }
            if (glowInside.booleanValue()) {
                RenderUtil.drawFadeGradientCircleOutline(radius.floatValue() * progress , size * outsideRadius.floatValue() * -0.2f, (int) (opacity.intValue() * (1 - progress)));
            }
            // Will be used in future
            //RenderUtil.drawCoolCircle(radius.floatValue() * progress, (int) (opacity.intValue() * (1 - progress)));
            RenderUtil.drawGradientCircleOutline(radius.floatValue() * progress, width.floatValue(), (int) (opacity.intValue() * (1 - progress)));
            restore();
        }
    }

    private void setup() {
        GlStateManager.pushMatrix();
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

    private void restore() {
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        glDepthMask(true);
        glDisable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.popMatrix();
    }

    static class Circle {
        private final Vec3d pos;
        private final long spawnTime;

        public Circle(Vec3d pos, long spawnTime) {
            this.pos = pos;
            this.spawnTime = spawnTime;
        }

        public double getProgress(double duration) {
            double currentTime = System.currentTimeMillis();
            return clamp((currentTime - spawnTime) / (duration * 1000.0), 0.0, 1.0);
        }
    }

    public enum Mode {
        Jump, Impact, Both
    }
}
