package xyz.templecheats.templeclient.features.module.modules.render;

import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;

public class Aspect extends Module {
    public static Aspect INSTANCE;

    /****************************************************************
     *                      Settings
     ****************************************************************/
    public final DoubleSetting width = new DoubleSetting("Width", this, 0.0, 16.0, 3.0);
    public final DoubleSetting height = new DoubleSetting("Height", this, 0.0, 16.0, 3.0);

    public Aspect() {
        super("Aspect", "Modify game screen width and height", Keyboard.KEY_NONE, Category.Render);
        INSTANCE = this;
        registerSettings(width, height);
    }
}
