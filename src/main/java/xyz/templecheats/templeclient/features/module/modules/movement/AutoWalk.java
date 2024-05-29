package xyz.templecheats.templeclient.features.module.modules.movement;

import net.minecraft.block.material.Material;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;

public class AutoWalk extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    public AutoWalk() {
        super("AutoWalk", "Automatically walks", Keyboard.KEY_NONE, Category.Movement);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
    }

    @Override
    public void onUpdate() {
        if (this.isEnabled()) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);

            if (isInWater()) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), true);
            } else {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
            }
        }
    }

    private boolean isInWater() {
        BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        return mc.world.getBlockState(playerPos).getMaterial() == Material.WATER;
    }
}