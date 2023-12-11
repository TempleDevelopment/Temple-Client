package xyz.templecheats.templeclient.features.modules.render;

import xyz.templecheats.templeclient.features.modules.Module;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class Tracers extends Module {
    public Tracers() {
        super("Tracers", Keyboard.KEY_NONE, Category.RENDER);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e) {
        for (Entity playerEntity : mc.world.playerEntities) {
            if (playerEntity != null && playerEntity != mc.player) {
                RenderUtil.trace(mc, playerEntity, mc.getRenderPartialTicks(), 1);
            }
        }
    }
}