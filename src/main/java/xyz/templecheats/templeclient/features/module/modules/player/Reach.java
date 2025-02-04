package xyz.templecheats.templeclient.features.module.modules.player;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

public class Reach extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final IntSetting reach = new IntSetting("Reach", this, 4, 8, 6);

    public Reach() {
        super("Reach", "Extends your interaction distance", Keyboard.KEY_NONE, Category.Player);
        registerSettings(reach);
    }

    public void onEnable() {
        EntityPlayer player = mc.player;
        IAttributeInstance setBlockReachDi = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE);
        player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).applyModifier(new AttributeModifier(player.getUniqueID(), "custom_reach", reach.intValue() - 5.0F, 1));
    }

    @Override
    public void onDisable() {
        mc.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).removeModifier(mc.player.getUniqueID());
    }
}