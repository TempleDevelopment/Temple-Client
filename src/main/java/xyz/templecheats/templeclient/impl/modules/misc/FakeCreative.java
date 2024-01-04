package xyz.templecheats.templeclient.impl.modules.misc;

import xyz.templecheats.templeclient.impl.modules.client.Panic;
import xyz.templecheats.templeclient.impl.modules.Module;
import net.minecraft.world.GameType;
import org.lwjgl.input.Keyboard;

public class FakeCreative extends Module {
    public FakeCreative() {
        super("FakeCreative", Keyboard.KEY_NONE, Category.MISC);
    }

    @Override
    public void onEnable() {
        mc.playerController.setGameType(GameType.CREATIVE);
    }

    @Override
    public void onDisable() {
        if (!Panic.isPanic) {
            mc.playerController.setGameType(GameType.SURVIVAL);
        }
    }
}