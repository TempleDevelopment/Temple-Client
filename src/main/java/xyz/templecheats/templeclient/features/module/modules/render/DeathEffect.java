package xyz.templecheats.templeclient.features.module.modules.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.render.Render3DPostEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.Colors;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;
import xyz.templecheats.templeclient.util.time.TimerUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import static net.minecraft.util.math.MathHelper.clamp;
import static org.lwjgl.opengl.GL11.*;
import static xyz.templecheats.templeclient.util.color.ColorUtil.setAlpha;
import static xyz.templecheats.templeclient.util.render.RenderUtil.renderTexture;
import static xyz.templecheats.templeclient.util.sound.SoundUtils.playSound;

// IDK
public class DeathEffect extends Module {
    private final BooleanSetting thunder = new BooleanSetting("Thunder", this, true);
    private final IntSetting numbersThunder = new IntSetting("Number Thunder", this , 1, 10, 1);
    private final BooleanSetting beam = new BooleanSetting("Beam", this, true);
    private final BooleanSetting beamSound = new BooleanSetting("Beam Sound", this, true);
    private final BooleanSetting skull = new BooleanSetting("Skull", this, true);
    private final BooleanSetting circle = new BooleanSetting("Circle", this, true);
    private final DoubleSetting radius = new DoubleSetting( "Radius", this, 0.5, 5.0, 1.5);
    private final DoubleSetting duration = new DoubleSetting( "Duration", this, 2.0, 10.0, 5.0);
    private final ArrayList<DrawCircle> circleList = new ArrayList<>();
    private final ArrayList<EntityPlayer> deadList = new ArrayList<>();
    private final TimerUtil thunderTimer = new TimerUtil();
    private final TimerUtil timer = new TimerUtil();
    public DeathEffect() {
        super("DeathEffect", "Spawns a thunder at player's location when they die", Keyboard.KEY_NONE, Category.Render);
        this.registerSettings(thunder, numbersThunder, beam, beamSound, skull, circle, radius, duration);
    }

    private void reset() {
        circleList.clear();
        deadList.clear();
    }

    @Override
    public void onEnable() {
       reset();
    }

    @Override
    public void onUpdate() {
        if (mc.world == null && mc.player == null) {
            reset();
            return;
        }
        mc.world.playerEntities.forEach(entity -> {
            if (deadList.contains(entity)) {
                if (entity.getHealth() > 0 && timer.passedMs((long) (2000L * duration.floatValue()))) {
                    deadList.remove(entity);
                }
            } else {
                if (entity.getHealth() == 0) {
                    deadList.add(entity);
                    circleList.add(new DrawCircle(entity.getPositionVector(), System.currentTimeMillis()));
                    if (beamSound.booleanValue()) {
                        circleList.forEach(it -> {
                            float progress = (float) it.getProgress(duration.floatValue());
                            playSound(it.pos , "beam" , !it.shouldFadeOut ? 0.8f * progress : 0.8f * (1 - progress) , 1f);
                        });
                    }
                    if (thunder.booleanValue() && thunderTimer.passedMs((long) (2000L * duration.floatValue()))) {
                        for (int i = 0; i < numbersThunder.intValue(); i++) {
                            mc.world.addWeatherEffect(new EntityLightningBolt(entity.world , entity.posX , entity.posY , entity.posZ , true));
                            mc.player.playSound(SoundEvents.ENTITY_LIGHTNING_THUNDER, 1f, 1.f);
                        }
                    }
                }
            }
        });
        circleList.removeIf(it -> it.getProgress(duration.floatValue()) > 2.005 * duration.floatValue());
        deadList.removeIf(it -> it.getHealth() > 0 && timer.passedMs((long) (2000 * duration.floatValue())));
    }

    @Listener
    public void onRender3D(Render3DPostEvent event) {
        try {
            circleList.forEach(it -> {
                it.beam();
                it.texture();
            });
        }catch (ConcurrentModificationException ignored) {
        }
    }

    class DrawCircle {
        private final Vec3d pos;
        private final long spawnTime;
        private final long maxScaleTime;
        private boolean shouldFadeOut = false;

        public DrawCircle(Vec3d pos , long spawnTime) {
            this.pos = pos;
            this.spawnTime = spawnTime;
            this.maxScaleTime = spawnTime + (long) (duration.floatValue() * 1000 * 0.8);
        }

        public double getProgress(double duration) {
            double currentTime = System.currentTimeMillis();
            return clamp((currentTime - spawnTime) / (duration * 1000.0) , 0.0 , 1.0);
        }

        public void beam() {
            beamSetup();
            double progress = Math.pow(getProgress(duration.floatValue()), 2);
            GL11.glTranslated(pos.x - mc.getRenderManager().viewerPosX, pos.y - mc.getRenderManager().viewerPosY, pos.z - mc.getRenderManager().viewerPosZ);
            if (beam.booleanValue()) drawBeam(!(shouldFadeOut) ? (float) (radius.floatValue() * (progress / 1.2)) : 0f , (float) (255 * (1 - getProgress(duration.floatValue()) * 3.7)) , 255F , shouldFadeOut ? 200 : (int) (200 * (1 - getProgress(duration.floatValue()))) , shouldFadeOut ? 200 : (int) (200 * (1 - getProgress(duration.floatValue()))));
            beamRestore();
        }

        public void texture() {
            double progress = getProgress(duration.floatValue());
            textureSetup();
            GL11.glTranslated(pos.x - mc.getRenderManager().viewerPosX, pos.y - mc.getRenderManager().viewerPosY, pos.z - mc.getRenderManager().viewerPosZ);
            GL11.glPushMatrix();
            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0, 1, 0);
            GlStateManager.scale(-1, -1, 1);
            GlStateManager.translate(-0.75, -3 * progress, -0.01);
            if (skull.booleanValue()) drawTexture("dead", progress);
            GL11.glPopMatrix();
            GlStateManager.rotate((float) (360 * (progress / 5)), 0, 1, 0);
            GlStateManager.rotate(90, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(-radius.floatValue() * progress, -radius.floatValue() * progress, radius.floatValue() * progress);
            GlStateManager.translate(-0.75, -0.75, -0.01);
            if (System.currentTimeMillis() >= maxScaleTime) {
                shouldFadeOut = true;
            }
            if (circle.booleanValue()) drawTexture("rune_1", progress); // Don't delete other rune I'll use after I'm back
            textureRestore();
        }

        private void drawTexture(String textureName, double progress) {
            int alpha = shouldFadeOut ? calculateFadeAlpha(progress) : (int) (255 * (1 - progress));
            renderTexture(new ResourceLocation("textures/" + textureName + ".png"),
                    setAlpha(Colors.INSTANCE.getGradient()[0], alpha),
                    setAlpha(Colors.INSTANCE.getGradient()[1], alpha));
        }

        private int calculateFadeAlpha(double progress) {
            double remainingTime = (spawnTime + (duration.floatValue() * 1000)) - System.currentTimeMillis();
            double fadeProgress = remainingTime / 1000.0;
            int alpha = (int) (255 * fadeProgress * progress);
            return Math.max(0, alpha);
        }

        public void drawBeam(float radius, float from, float to, int alpha1, int alpha2) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();

            GlStateManager.color(-1f, -1f, -1f, -1f);
            buffer.begin(GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            for (int i = 0; i <= 360; i++) {
                Color color = setAlpha(Colors.INSTANCE.getGradient()[1], alpha1);
                Color color1 = setAlpha(Colors.INSTANCE.getGradient()[0], alpha2);

                double dir = Math.toRadians(i - 180.0);
                double x = Math.cos(dir) * radius;
                double z = Math.sin(dir) * radius;

                buffer.pos(x, from, z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
                buffer.pos(x, to, z).color(color1.getRed(), color1.getGreen(), color1.getBlue(), color1.getAlpha()).endVertex();
            }
            tessellator.draw();
        }
    }

    private void beamSetup() {
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

    private void beamRestore() {
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

    public void textureSetup() {
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.shadeModel(7425);
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
    }

    public void textureRestore() {
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
    }
}