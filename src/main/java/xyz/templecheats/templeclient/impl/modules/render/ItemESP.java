package xyz.templecheats.templeclient.impl.modules.render;

import xyz.templecheats.templeclient.impl.modules.Module;
import xyz.templecheats.templeclient.api.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class ItemESP extends Module {
    AxisAlignedBB box;

    public ItemESP() {
        super("ItemESP","Highlights items", Keyboard.KEY_NONE, Category.RENDER);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e) {
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityItem) {
                EntityItem itemEntity = (EntityItem) entity;
                String itemName = itemEntity.getItem().getDisplayName();

                double x = itemEntity.lastTickPosX + (itemEntity.posX - itemEntity.lastTickPosX) * Minecraft.getMinecraft().getRenderPartialTicks();
                double y = itemEntity.lastTickPosY + (itemEntity.posY - itemEntity.lastTickPosY) * Minecraft.getMinecraft().getRenderPartialTicks();
                double z = itemEntity.lastTickPosZ + (itemEntity.posZ - itemEntity.lastTickPosZ) * Minecraft.getMinecraft().getRenderPartialTicks();

                mc.fontRenderer.drawStringWithShadow(itemName, (float)x, (float)y, 0xFFFFFF);
                box = new AxisAlignedBB(
                        entity.getEntityBoundingBox().minX
                                - 0.05
                                - entity.posX
                                + ((float) ((double) ((float) entity.lastTickPosX) + (entity.posX - entity.lastTickPosX) * Minecraft.getMinecraft().getRenderPartialTicks()) - Minecraft.getMinecraft()
                                .getRenderManager().viewerPosX),
                        entity.getEntityBoundingBox().minY
                                - entity.posY
                                + ((float) ((double) ((float) entity.lastTickPosY) + (entity.posY - entity.lastTickPosY) * Minecraft.getMinecraft().getRenderPartialTicks()) - Minecraft.getMinecraft()
                                .getRenderManager().viewerPosY),
                        entity.getEntityBoundingBox().minZ
                                - 0.05
                                - entity.posZ
                                + ((float) ((double) ((float) entity.lastTickPosZ) + (entity.posZ - entity.lastTickPosZ) * Minecraft.getMinecraft().getRenderPartialTicks()) - Minecraft.getMinecraft()
                                .getRenderManager().viewerPosZ),
                        entity.getEntityBoundingBox().maxX
                                + 0.05
                                - entity.posX
                                + ((float) ((double) ((float) entity.lastTickPosX) + (entity.posX - entity.lastTickPosX) * Minecraft.getMinecraft().getRenderPartialTicks()) - Minecraft.getMinecraft()
                                .getRenderManager().viewerPosX),
                        entity.getEntityBoundingBox().maxY
                                + 0.1
                                - entity.posY
                                + ((float) ((double) ((float) entity.lastTickPosY) + (entity.posY - entity.lastTickPosY) * Minecraft.getMinecraft().getRenderPartialTicks()) - Minecraft.getMinecraft()
                                .getRenderManager().viewerPosY),
                        entity.getEntityBoundingBox().maxZ
                                + 0.05
                                - entity.posZ
                                + ((float) ((double) ((float) entity.lastTickPosZ) + (entity.posZ - entity.lastTickPosZ) * Minecraft.getMinecraft().getRenderPartialTicks()) - Minecraft.getMinecraft()
                                .getRenderManager().viewerPosZ));

                RenderUtil.FillOnlyLine(entity, box);
            }
        }
    }
}