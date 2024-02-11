package xyz.templecheats.templeclient.api.util;

import net.minecraft.client.Minecraft;

import java.util.Random;

public interface Globals {
    Minecraft mc = Minecraft.getMinecraft();
    Random random = new Random();
    char SECTIONSIGN = '\u00A7';

    default public boolean nullCheck(){
        return mc.player == null || mc.world == null;
    }

}