package com.example.examplemod.Module.RENDER;

import com.example.examplemod.Module.Module;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class NameTags extends Module {
    public NameTags() {
        super("NameTags", Keyboard.KEY_NONE, Category.RENDER);
    }

    @SubscribeEvent
    public void onRender(RenderLivingEvent.Specials.Pre e) {
        EntityLivingBase entity = e.getEntity();

        if (!(entity instanceof EntityPlayer) || entity == mc.player) {
            return;
        }
        if (entity.isDead || entity.getHealth() < 0 || entity.isInvisible()) {
            return;
        }

        GL11.glPushMatrix();
        Vec3d pos = new Vec3d(e.getX(), e.getY() + entity.height / 1.5, e.getZ());
        GL11.glTranslated(pos.x, pos.y + 1, pos.z);

        double scale = Math.max(1, pos.distanceTo(new Vec3d(0, 0, 0)) / 6);
        GL11.glScaled(scale, scale, scale);
        int health = (int) Math.ceil(entity.getHealth());

        StringBuilder nameplate = new StringBuilder(entity.getDisplayName().getFormattedText() + " §c" + health + "§r");
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            nameplate.append("\n");

            // Display armor information
            for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                ItemStack stack = player.getItemStackFromSlot(slot);
                if (!stack.isEmpty()) {
                    nameplate.append(stack.getDisplayName()).append(" ");
                }
            }
        }

        EntityRenderer.drawNameplate(mc.fontRenderer, nameplate.toString(), 0, 0, 0, 0,
                mc.getRenderManager().playerViewY,
                mc.getRenderManager().playerViewX,
                mc.gameSettings.thirdPersonView == 2, false
        );
        GL11.glPopMatrix();
        e.setCanceled(true);
    }
}