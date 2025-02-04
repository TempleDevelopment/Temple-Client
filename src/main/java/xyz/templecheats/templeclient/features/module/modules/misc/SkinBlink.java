package xyz.templecheats.templeclient.features.module.modules.misc;

import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

import java.util.Random;

public class SkinBlink extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", this, Mode.Horizontal);

    /****************************************************************
     *                      Variables
     ****************************************************************/
    private Random r;
    private int len = EnumPlayerModelParts.values().length;
    private int slowness = 2;

    public SkinBlink() {
        super("SkinBlink", "Flash skin layers on and off", Keyboard.KEY_NONE, Category.Misc);
        r = new Random();
        registerSettings(mode);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.player == null || mc.player.ticksExisted % slowness != 0) {
            return;
        }

        mc.gameSettings.switchModelPartEnabled(EnumPlayerModelParts.values()[this.r.nextInt(this.len)]);
    }

    public enum Mode {
        Horizontal(new EnumPlayerModelParts[]{
                EnumPlayerModelParts.LEFT_SLEEVE, EnumPlayerModelParts.JACKET,
                EnumPlayerModelParts.HAT, EnumPlayerModelParts.LEFT_PANTS_LEG, EnumPlayerModelParts.RIGHT_PANTS_LEG,
                EnumPlayerModelParts.RIGHT_SLEEVE
        }),
        Vertical(new EnumPlayerModelParts[]{
                EnumPlayerModelParts.HAT, EnumPlayerModelParts.JACKET,
                EnumPlayerModelParts.LEFT_SLEEVE, EnumPlayerModelParts.RIGHT_SLEEVE,
                EnumPlayerModelParts.LEFT_PANTS_LEG, EnumPlayerModelParts.RIGHT_PANTS_LEG
        });

        private EnumPlayerModelParts[] parts;

        Mode(EnumPlayerModelParts[] parts) {
            this.parts = parts;
        }

        public EnumPlayerModelParts[] getParts() {
            return parts;
        }
    }
}