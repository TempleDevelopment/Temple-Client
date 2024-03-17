package xyz.templecheats.templeclient.features.module.modules.misc;

import net.minecraft.world.GameType;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.Panic;

public class FakeCreative extends Module {
    public FakeCreative() {
        super("FakeCreative","Client sided creative", Keyboard.KEY_NONE, Category.Miscelleaneous);
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