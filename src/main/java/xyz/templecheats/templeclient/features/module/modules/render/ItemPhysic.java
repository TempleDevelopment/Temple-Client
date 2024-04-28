package xyz.templecheats.templeclient.features.module.modules.render;

import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;

public class ItemPhysic extends Module {
    public static ItemPhysic INSTANCE;

    public final DoubleSetting scale = new DoubleSetting("Scale", this, 0.1, 5.0, 0.34);
    public final DoubleSetting rotateSpeed = new DoubleSetting("Rotate Speed", this, 0.0D, 100.0D, 25.0D);
    public final BooleanSetting oldRotation = new BooleanSetting("Old Rotation", this, false);

    public long tick;

    public ItemPhysic() {
        super("ItemPhysic", "Apply physic to your drop items", Keyboard.KEY_NONE, Category.Render);
        registerSettings(oldRotation, scale, rotateSpeed);
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        tick = System.nanoTime();
    }
}
