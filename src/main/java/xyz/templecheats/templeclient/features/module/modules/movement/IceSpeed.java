package xyz.templecheats.templeclient.features.module.modules.movement;

import net.minecraft.init.Blocks;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;

public class IceSpeed extends Module {
    private final DoubleSetting speed = new DoubleSetting("Speed", this, 0.1f, 1.5f, 0.4f);

    public IceSpeed() {
        super("IceSpeed", "Go fast on ice", Keyboard.KEY_NONE, Category.Movement);
        registerSettings(speed);
    }


    public void onUpdate() {
        Blocks.ICE.slipperiness = (float) this.speed.doubleValue();
        Blocks.PACKED_ICE.slipperiness = (float) this.speed.doubleValue();
        Blocks.FROSTED_ICE.slipperiness = (float) this.speed.doubleValue();
    }

    public void onDisable() {
        Blocks.ICE.slipperiness = 0.98f;
        Blocks.PACKED_ICE.slipperiness = 0.98f;
        Blocks.FROSTED_ICE.slipperiness = 0.98f;
    }
}