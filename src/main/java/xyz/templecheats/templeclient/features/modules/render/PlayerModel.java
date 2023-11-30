package xyz.templecheats.templeclient.features.modules.render;

import xyz.templecheats.templeclient.features.modules.Module;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class PlayerModel extends Module {
    public PlayerModel() {
        super("PlayerModel", Keyboard.KEY_NONE, Module.Category.RENDER);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        switch (event.getType()) {
            case TEXT:
                RenderUtil.renderEntity(mc.player, 30, 40, 100);
                break;
            default:
                break;
        }
    }
}