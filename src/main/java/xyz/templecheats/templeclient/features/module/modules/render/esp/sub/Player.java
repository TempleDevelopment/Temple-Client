package xyz.templecheats.templeclient.features.module.modules.render.esp.sub;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.Colors;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Player extends Module {
    /*
     * Settings
     */
    private final EnumSetting < Mode > mode = new EnumSetting < > ("Mode", this, Mode.Box);

    /*
     * Variables
     */
    private static final List < Entity > glowed = new ArrayList < > ();

    public Player() {
        super("Player", "Highlights players", Keyboard.KEY_NONE, Category.Render, true);

        registerSettings(mode);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e) {
        for (Entity entity: Minecraft.getMinecraft().world.playerEntities) {
            if (entity != Minecraft.getMinecraft().player && entity != null) {
                float r, g, b;
                if (TempleClient.friendManager.isFriend(entity.getName())) {
                    Color friendColor = Colors.INSTANCE.friendColor.getColor();
                    r = friendColor.getRed() / 255.0f;
                    g = friendColor.getGreen() / 255.0f;
                    b = friendColor.getBlue() / 255.0f;
                } else {
                    Color defaultColor = Colors.INSTANCE.staticColor.getColor();
                    r = defaultColor.getRed() / 255.0f;
                    g = defaultColor.getGreen() / 255.0f;
                    b = defaultColor.getBlue() / 255.0f;
                }

                if (mode.value() == Mode.Box) {
                    if (!glowed.isEmpty()) {
                        for (Entity glowEntity: glowed) {
                            glowEntity.setGlowing(false);
                        }
                        glowed.clear();
                    }
                    AxisAlignedBB box = new AxisAlignedBB(
                            entity.getEntityBoundingBox().minX - 0.05 - entity.posX +
                                    (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * e.getPartialTicks() - Minecraft.getMinecraft().getRenderManager().viewerPosX),
                            entity.getEntityBoundingBox().minY - entity.posY +
                                    (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * e.getPartialTicks() - Minecraft.getMinecraft().getRenderManager().viewerPosY),
                            entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ +
                                    (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * e.getPartialTicks() - Minecraft.getMinecraft().getRenderManager().viewerPosZ),
                            entity.getEntityBoundingBox().maxX + 0.05 - entity.posX +
                                    (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * e.getPartialTicks() - Minecraft.getMinecraft().getRenderManager().viewerPosX),
                            entity.getEntityBoundingBox().maxY + 0.1 - entity.posY +
                                    (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * e.getPartialTicks() - Minecraft.getMinecraft().getRenderManager().viewerPosY),
                            entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ +
                                    (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * e.getPartialTicks() - Minecraft.getMinecraft().getRenderManager().viewerPosZ)
                    );
                    RenderUtil.FillOnlyLinePlayerESP(entity, box, r, g, b);
                } else {
                    entity.setGlowing(true);
                    if (!glowed.contains(entity)) {
                        glowed.add(entity);
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        for (Entity entity: glowed) {
            entity.setGlowing(false);
        }
        glowed.clear();
        super.onDisable();
    }

    private enum Mode {
        Box,
        Glow
    }
}