package xyz.templecheats.templeclient.impl.modules.player;

import xyz.templecheats.templeclient.impl.modules.Module;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;

public class Reach extends Module {
    public Reach() {
        super("Reach","Extends the player's reach", Keyboard.KEY_NONE, Category.Player);
    }

    public void onEnable() {
        EntityPlayer player = mc.player;
        IAttributeInstance setBlockReachDi = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE);
        player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).applyModifier(new AttributeModifier(player.getUniqueID(), "custom_reach", 0.5F, 1));
    }

    @Override
    public void onDisable() {
        mc.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).removeModifier(mc.player.getUniqueID());
    }
}