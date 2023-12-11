package xyz.templecheats.templeclient.features.modules.client.hud;

import xyz.templecheats.templeclient.features.modules.Module;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class PlayerModel extends Module {
    public static final PlayerModel INSTANCE = new PlayerModel();
    public PlayerModel() {
        super("PlayerModel", Keyboard.KEY_NONE, Module.Category.CLIENT);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        switch (event.getType()) {
            case TEXT:
                RenderUtil.renderEntity(mc.player, 28, 30, 80);
                break;
            default:
                break;
        }
    }
}