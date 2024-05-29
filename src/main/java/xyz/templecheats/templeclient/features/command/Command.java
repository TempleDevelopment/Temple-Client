package xyz.templecheats.templeclient.features.command;

import net.minecraft.client.Minecraft;

public abstract class Command {
    private static final Minecraft mc = Minecraft.getMinecraft();

    /****************************************************************
     *                      Abstract Methods
     ****************************************************************/
    public abstract String getName();
    public abstract void execute(String[] args);
}
