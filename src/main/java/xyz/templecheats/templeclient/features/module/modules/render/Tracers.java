package xyz.templecheats.templeclient.features.module.modules.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.Colors;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;

public class Tracers extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final BooleanSetting colorDistance = new BooleanSetting("Color Distance", this, false);

    public Tracers() {
        super("Tracers", "Draws lines to entities", Keyboard.KEY_NONE, Category.Render);

        registerSettings(colorDistance);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e) {
        for (Entity playerEntity : mc.world.playerEntities) {
            if (playerEntity != null && playerEntity != mc.player) {
                float r = Colors.INSTANCE.getColor().getRed() / 255.0f;
                float g = Colors.INSTANCE.getColor().getGreen() / 255.0f;
                float b = Colors.INSTANCE.getColor().getBlue() / 255.0f;

                if (colorDistance.booleanValue()) {
                    float distance = mc.player.getDistance(playerEntity);
                    float lerpFactor = Math.min(distance / 50.0f, 1.0f);

                    r = lerp(0.0f, 1.0f, lerpFactor);
                    g = lerp(1.0f, 0.0f, lerpFactor);
                    b = 0.0f;
                }

                RenderUtil.trace(mc, playerEntity, mc.getRenderPartialTicks(), 1, r, g, b);
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
    }

    private float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }
}