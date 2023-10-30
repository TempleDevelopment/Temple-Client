package com.example.examplemod.Module.MOVEMENT;

import com.example.examplemod.Module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class Spider extends Module {
    public Spider() {
        super("Spider", Keyboard.KEY_NONE, Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (mc.player.collidedHorizontally) {
            mc.player.motionY = 0.25;
        }
    }
}