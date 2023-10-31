package com.example.examplemod.Module.RENDER;

import com.example.examplemod.Module.Module;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.lwjgl.input.Keyboard;

import java.util.Objects;

public class FullBright extends Module {
    // private float oldBright;

    public FullBright() {
        super("FullBright[B]", Keyboard.KEY_B, Category.RENDER);
    }

    @Override
    public void onEnable() {
        /*
        oldBright = mc.gameSettings.gammaSetting;
        mc.gameSettings.gammaSetting = 100;
         */


        mc.player.addPotionEffect(new PotionEffect(Objects.requireNonNull(Potion.getPotionById(16)), 999999, 1));
    }

    @Override
    public void onDisable() {
        /*
        mc.gameSettings.gammaSetting = oldBright;
         */

        mc.player.removePotionEffect(Objects.requireNonNull(Potion.getPotionById(16)));
    }
}