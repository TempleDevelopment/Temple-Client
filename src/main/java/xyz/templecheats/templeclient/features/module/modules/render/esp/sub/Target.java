package xyz.templecheats.templeclient.features.module.modules.render.esp.sub;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.Colors;
import xyz.templecheats.templeclient.features.module.modules.combat.Aura;
import xyz.templecheats.templeclient.features.module.modules.combat.AutoCrystal;
import xyz.templecheats.templeclient.util.setting.impl.*;
import java.awt.*;

import static xyz.templecheats.templeclient.util.render.RenderUtil.interpolateEntity;
import static xyz.templecheats.templeclient.util.math.MathUtil.lerp;
import static xyz.templecheats.templeclient.util.render.RenderUtil.renderTexture;

public class Target extends Module {
    /*
     * Settings
     */
    private final EnumSetting < ESP > texture = new EnumSetting < > ("Texture", this, ESP.TriangleCapture);
    private final DoubleSetting scale = new DoubleSetting("Scale", this, 0.3, 1.0, 0.75);
    private final DoubleSetting maxSpin = new DoubleSetting("MaxSpin", this, 10.0, 30.0, 15.0);
    private final DoubleSetting opacity = new DoubleSetting("Opacity", this, 0.1, 1, 0.5);

    // TODO: Rewrite this shit
    /*
     * Variables
     */
    boolean flip = false;
    float spinSpeed = 1.0f;
    float prevSpinStep = 1.0f;
    float spinStep = 1.0f;

    boolean isScaling = false;
    float initialScale = 2.5F;
    float defaultScale = 0.0F;
    float targetScale = scale.floatValue();
    long scaleAnimationStartTime = 0L;
    private EntityLivingBase TARGET = null;

    public Target() {
        super("Target", "Renders a image at players core", Keyboard.KEY_NONE, Category.Render, true);
        registerSettings(scale, maxSpin, opacity, texture);
    }

    private void resetScale() {
        isScaling = false;
        defaultScale = targetScale;
    }

    private void scaleAnimation() {
        if (!isScaling && TARGET != null) {
            isScaling = true;
            scaleAnimationStartTime = System.currentTimeMillis();
        }

        if (isScaling) {
            float elapsed = System.currentTimeMillis() - scaleAnimationStartTime;

            if (elapsed < 1500L) {
                defaultScale = initialScale - (initialScale - targetScale) * (elapsed / 1500L);
            } else {
                defaultScale = scale.floatValue();
            }
        }
    }

    private void spinAnimation() {
        if (spinSpeed > maxSpin.floatValue() || spinSpeed < -maxSpin.floatValue()) {
            flip = !flip;
        }

        spinSpeed += flip ? -0.2F : 0.2F;

        prevSpinStep = spinStep;
        spinStep += spinSpeed;
    }

    @Override
    public void onEnable() {
        resetScale();
    }

    @Override
    public void onDisable() {
        resetScale();
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null || TARGET == null) {
            resetScale();
        }
        EntityLivingBase aura = Aura.INSTANCE.isEnabled() ? Aura.INSTANCE.renderTarget : null;
        EntityLivingBase ca = AutoCrystal.INSTANCE.isEnabled() ? AutoCrystal.INSTANCE.getTarget() : null;

        TARGET = (aura != null) ? aura : ca;

        spinAnimation();
        scaleAnimation();
    }

    @Override
    public void onRenderWorld(float partialTicks) {
        if (mc.player == null || mc.world == null || TARGET == null) {
            return;
        }

        Color color1 = Colors.INSTANCE.getGradient()[0];
        Color color2 = Colors.INSTANCE.getGradient()[1];
        int alpha = (int) Math.round(opacity.doubleValue() * 255);
        Color color1WithOpacity = new Color(color1.getRed(), color1.getGreen(), color1.getBlue(), alpha);
        Color color2WithOpacity = new Color(color2.getRed(), color2.getGreen(), color2.getBlue(), alpha);

        if (TARGET != Minecraft.getMinecraft().player && TARGET != null) {
            spinTargetTexture(
                    texture.value().getTexture(),
                    TARGET,
                    prevSpinStep,
                    spinStep,
                    defaultScale,
                    color1WithOpacity,
                    color2WithOpacity
            );
        }
    }


    private void spinTargetTexture(
            ResourceLocation resourceLocation,
            Entity entity,
            Float prevSpinStep,
            Float spinStep,
            Float scale,
            Color color1,
            Color color2
    ) {
        Vec3d pos = interpolateEntity(entity);

        setUp();
        GlStateManager.translate(pos.x, pos.y + 1, pos.z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0, 1, 0);
        GlStateManager.rotate((mc.gameSettings.thirdPersonView == 2 ? -1F : 1F) * mc.getRenderManager().playerViewX, 1, 0, 0);
        GlStateManager.rotate(lerp(prevSpinStep, spinStep, mc.getRenderPartialTicks()), 0.0F, 0.0F, 1.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.translate(-0.75, -0.75, -0.01);
        renderTexture(resourceLocation, color1, color2);
        restore();
    }

    private void setUp() {
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.shadeModel(7425);
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
    }

    private void restore() {
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
    }

    public enum ESP {
        SquareCapture("textures/esp/square-capture.png"),
        SquareDashed("textures/esp/square-dashes.png"),
        TriangleCapture("textures/esp/triangle-capture.png"),
        TriangleDashed("textures/esp/triangle-dashes.png");

        private final ResourceLocation texture;

        ESP(String texturePath) {
            this.texture = new ResourceLocation(texturePath);
        }

        public ResourceLocation getTexture() {
            return texture;
        }
    }
}