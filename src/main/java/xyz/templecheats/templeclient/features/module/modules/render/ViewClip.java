package xyz.templecheats.templeclient.features.module.modules.render;


import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;

public class ViewClip extends Module {
    public static ViewClip INSTANCE;
    /****************************************************************
     *                      Settings
     ****************************************************************/
    public final DoubleSetting distance = new DoubleSetting("Distance", this, 0.0, 50.0, 10.0);

    public ViewClip() {
        super("ViewClip", "Ignores block collision for the camera in third person", Keyboard.KEY_NONE, Category.Render);
        INSTANCE = this;
        registerSettings(distance);
    }
}