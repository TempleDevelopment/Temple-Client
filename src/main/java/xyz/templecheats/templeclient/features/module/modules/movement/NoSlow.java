package xyz.templecheats.templeclient.features.module.modules.movement;

import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

public class NoSlow extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", this, Mode.NCP);
    public final IntSetting speed = new IntSetting("Speed", this, 1, 100, 100);


    public NoSlow() {
        super("NoSlow", "Removes item slow down", Keyboard.KEY_NONE, Category.Movement);
        registerSettings(speed, mode);
    }

    @SubscribeEvent
    public void onInput(InputUpdateEvent e) {
        if (!(mode.value() == Mode.StrictNCP && mode.value() == Mode.NCP) && mc.player.isHandActive() && !mc.player.isRiding()) {
            mc.player.movementInput.moveForward *= (5f * (speed.intValue() / 100f));
            mc.player.movementInput.moveStrafe *= (5f * (speed.intValue() / 100f));
        }

        if (mode.value() == Mode.StrictNCP || mode.value() == Mode.NCP) {
            if (mc.player.isHandActive() && !mc.player.isRiding() && !mc.player.isSneaking()) {
                if (mode.value() == Mode.StrictNCP && (mc.player.getHeldItemMainhand().getItem() instanceof ItemFood || mc.player.getHeldItemOffhand().getItem() instanceof ItemFood))
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                mc.player.movementInput.moveForward /= 0.2;
                mc.player.movementInput.moveStrafe /= 0.2;
            }
        }

        if (mode.value() == Mode.Matrix && mc.player.isHandActive()) {
            {
                if (mc.player.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) {
                    if (mc.player.ticksExisted % 2 == 0) {
                        mc.player.motionX *= 0.46;
                        mc.player.motionZ *= 0.46;
                    }
                } else if ((double) mc.player.fallDistance > 0.2) {
                    mc.player.motionX *= 0.9100000262260437;
                    mc.player.motionZ *= 0.9100000262260437;
                }
            }
        }
        /*if (mode.value() == Mode.Grim && mc.player.isHandActive()) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
            mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem < 8 ? mc.player.inventory.currentItem + 1 : mc.player.inventory.currentItem - 1));
        }*/
    }

    public enum Mode {
        NCP,
        StrictNCP,
        Matrix,
        //Grim
    }
}