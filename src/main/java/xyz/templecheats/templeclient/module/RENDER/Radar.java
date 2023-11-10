package xyz.templecheats.templeclient.module.RENDER;

import xyz.templecheats.templeclient.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.Color;
import java.util.List;

public class Radar extends Module {
    public Radar() {
        super("Radar", Keyboard.KEY_NONE, Category.RENDER); // Adjust the key binding if needed
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            renderRadar();
        }
    }

    private void renderRadar() {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int width = scaledResolution.getScaledWidth();
        int height = scaledResolution.getScaledHeight();

        List<Entity> entities = mc.world.loadedEntityList;

        for (Entity entity : entities) {
            if (entity instanceof EntityPlayer) {
                // Render purple dot for players
                renderDot(entity, width, height, Color.MAGENTA);
            } else if (entity instanceof EntityAnimal) {
                // Render green dot for animals
                renderDot(entity, width, height, Color.GREEN);
            } else if (entity instanceof EntityMob) {
                // Render red dot for mobs
                renderDot(entity, width, height, Color.RED);
            }
        }
    }

    private void renderDot(Entity entity, int radarWidth, int radarHeight, Color color) {
        double playerX = Minecraft.getMinecraft().player.posX;
        double playerZ = Minecraft.getMinecraft().player.posZ;
        double entityX = entity.posX;
        double entityZ = entity.posZ;
        double distanceX = playerX - entityX;
        double distanceZ = playerZ - entityZ;
        double distance = Math.sqrt(distanceX * distanceX + distanceZ * distanceZ);
        double angle = Math.toDegrees(Math.atan2(distanceZ, distanceX));
        angle += Minecraft.getMinecraft().player.rotationYaw;

        int x = radarWidth / 2 + (int) (Math.cos(Math.toRadians(angle)) * distance);
        int y = radarHeight / 2 + (int) (Math.sin(Math.toRadians(angle)) * distance);

        int dotSize = 2; // Adjust the size of the dot

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        Gui.drawRect(-dotSize / 2, -dotSize / 2, dotSize / 2, dotSize / 2, color.getRGB());
        GlStateManager.popMatrix();
    }
}
