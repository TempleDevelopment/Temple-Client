package xyz.templecheats.templeclient.features.module.modules.world;

import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;

public class LiquidInteract extends Module {
    public LiquidInteract() {
        super("LiquidInteract", "Place blocks on top of water", Keyboard.KEY_NONE, Module.Category.World);
    }
}
