package xyz.templecheats.templeclient.impl.modules.render;

import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.impl.modules.Module;

public final class Freecam extends Module {
    public Freecam() {
        super("FreeCam", Keyboard.KEY_NONE, Category.RENDER);
    }

}