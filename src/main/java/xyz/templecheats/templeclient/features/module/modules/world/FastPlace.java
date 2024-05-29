package xyz.templecheats.templeclient.features.module.modules.world;

import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.mixins.accessor.IMixinMinecraft;
import xyz.templecheats.templeclient.mixins.accessor.IPlayerControllerMP;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

public class FastPlace extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final EnumSetting<FastPlaceMode> mode = new EnumSetting<>("Mode", this, FastPlaceMode.Everything);

    public FastPlace() {
        super("FastPlace", "Place blocks faster", Keyboard.KEY_NONE, Module.Category.World);
        registerSettings(mode);
    }

    @Override
    public void onUpdate() {
        switch (mode.value()) {
            case Crystals:
                if (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
                    ((IMixinMinecraft) mc).setRightClickDelayTimer(0);
                }
                break;
            case OffhandCrystals:
                if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
                    ((IMixinMinecraft) mc).setRightClickDelayTimer(0);
                }
                break;
            case Blocks:
                if (mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock) {
                    ((IMixinMinecraft) mc).setRightClickDelayTimer(0);
                }
                break;
            case Everything:
                ((IMixinMinecraft) mc).setRightClickDelayTimer(0);
                break;
        }

        ((IPlayerControllerMP) mc.playerController).setBlockHitDelay(0);
    }

    public enum FastPlaceMode {
        Crystals,
        OffhandCrystals,
        Blocks,
        Everything
    }
}
