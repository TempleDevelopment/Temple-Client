package xyz.templecheats.templeclient.features.module.modules.render;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.event.events.player.ModelEvent;
import xyz.templecheats.templeclient.event.events.render.Render3DPostEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.render.animation.Animation;
import xyz.templecheats.templeclient.util.render.animation.Easing;
import xyz.templecheats.templeclient.util.render.shader.impl.GradientShader;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.opengl.GL11.*;

public class PopChams extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final BooleanSetting self = new BooleanSetting("Self", this, false);
    private final IntSetting duration = new IntSetting("Duration", this, 50, 5000, 2000);
    private final EnumSetting<Direction> direction = new EnumSetting<>("Direction", this, Direction.NONE);
    private final DoubleSetting movementHeight = new DoubleSetting("Height", this, 2.0, 10.0, 4.5);
    private final EnumSetting<Easing> easing = new EnumSetting<>("Easing", this, Easing.Linear);
    private final IntSetting startAlpha = new IntSetting("Start Alpha", this, 0, 255, 200);
    private final DoubleSetting outlineWidth = new DoubleSetting("Width", this, 0.1, 10.0, 3.5);

    /****************************************************************
     *                      Variables
     ****************************************************************/
    private final List<ModelInfo> chams = new CopyOnWriteArrayList<>();
    private final Map<EntityPlayer, ModelInfo> chamCache = new ConcurrentHashMap<>();
    public static boolean rendering;

    public PopChams() {
        super("PopChams", "Renders a totempop effect", Keyboard.KEY_NONE, Category.Render);
        registerSettings(self, duration, startAlpha, movementHeight, outlineWidth, direction, easing);
    }

    @Override
    public void onEnable() {
        this.chams.clear();
        this.chamCache.clear();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        this.chams.removeIf(chams -> System.currentTimeMillis() - chams.startTime > this.duration.intValue());
    }

    @Listener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketEntityStatus) {
            final SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if (packet == null) return;
            final Entity entity = packet.getEntity(mc.world);
            if (entity instanceof EntityPlayer && packet.getOpCode() == 35 && entity.isEntityAlive()) {
                invokeEntity((EntityPlayer) entity);
            }
        }
    }

    private void invokeEntity(EntityPlayer entity) {
        if (!this.self.booleanValue() && entity == mc.player) {
            return;
        }

        ModelInfo cham = chamCache.get(entity);
        if (cham == null) {
            return;
        }

        chams.add(new ModelInfo(
                cham.model,
                new EntityOtherPlayerMP(
                        cham.entity.world,
                        entity.getGameProfile()
                ) {{
                    copyLocationAndAnglesFrom(cham.entity);
                }},
                cham.limbSwing,
                cham.limbSwingAmount,
                cham.ageInTicks,
                cham.netHeadYaw,
                cham.headPitch,
                cham.scale
        ));
    }


    @Listener
    public void onModel(ModelEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            this.chamCache.put((EntityPlayer) event.getEntity(), new ModelInfo(
                    (ModelPlayer) event.getModelBase(), event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScale()
            ));
        }
    }

    @Listener
    public void onRender3D(Render3DPostEvent event) {
        this.chams.forEach(cham -> {
            cham.animation.progress(movementHeight.floatValue());
            float animFac = (float) cham.animation.getProgress();
            float alpha = startAlpha.intValue() / 255f;
            rendering = false;
            GradientShader.setup(Math.max(0.0f, alpha - (Math.min(Math.max(0.0f, animFac), alpha))));
            rendering = true;

            glPushMatrix();
            mc.getRenderManager().setRenderShadow(true);
            glTranslated((mc.getRenderManager().viewerPosX - cham.entity.posX) * -1.0, 1.4 + ((mc.getRenderManager().viewerPosY - cham.entity.posY) * -1.0), (mc.getRenderManager().viewerPosZ - cham.entity.posZ) * -1.0);

            double yOffset = 0.0;
            if (this.direction.value() == Direction.DOWN) {
                yOffset = -this.movementHeight.doubleValue();
            } else if (this.direction.value() == Direction.UP) {
                yOffset = this.movementHeight.doubleValue();
            }
            glTranslated(0.0, yOffset * animFac, 0.0);

            // Flipping and setting the correct rotation and scale
            glRotatef(180F, 1F, 0F, 0F);
            glRotatef(-cham.netHeadYaw, 0F, 1F, 0F);
            glScalef(0.95F, 0.95F, 0.95F);

            glPushMatrix();
            glEnable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            glDisable(GL_DEPTH_TEST);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            cham.model.render(cham.entity, cham.limbSwing, cham.limbSwingAmount, cham.ageInTicks, cham.netHeadYaw, cham.headPitch, cham.scale);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);
            glPopMatrix();

            glPushMatrix();
            glEnable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            glDisable(GL_DEPTH_TEST);
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            glEnable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
            glLineWidth((float) outlineWidth.doubleValue());
            cham.model.render(cham.entity, cham.limbSwing, cham.limbSwingAmount, cham.ageInTicks, cham.netHeadYaw, cham.headPitch, cham.scale);
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);
            glPopMatrix();
            mc.getRenderManager().setRenderShadow(true);
            glPopMatrix();

            GradientShader.finish();
            rendering = false;
        });
    }

    private enum Direction {
        NONE, UP, DOWN
    }

    private class ModelInfo {
        private final ModelPlayer model;
        private final Entity entity;
        private final float limbSwing;
        private final float limbSwingAmount;
        private final float ageInTicks;
        private final float netHeadYaw;
        private final float headPitch;
        private final float scale;
        private final long startTime = System.currentTimeMillis();
        private final Animation animation;

        public ModelInfo(ModelPlayer model, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            this.model = model;
            this.entity = entity;
            this.limbSwing = limbSwing;
            this.limbSwingAmount = limbSwingAmount;
            this.ageInTicks = ageInTicks;
            this.netHeadYaw = netHeadYaw;
            this.headPitch = headPitch;
            this.scale = scale;
            this.animation = new Animation(easing.value(), duration.intValue());
        }
    }
}